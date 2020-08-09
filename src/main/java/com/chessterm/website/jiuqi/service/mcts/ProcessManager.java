package com.chessterm.website.jiuqi.service.mcts;

import com.chessterm.website.jiuqi.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ProcessManager {

    private static ProcessManager instance = null;

    private static final int maxTime = 300000;  // Kill after 5 minutes.

    protected Map<Process, ProcessMeta> processes = new ConcurrentHashMap<>();

    private ProcessManager() {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(this.processWatcher(), 100, 500, TimeUnit.MILLISECONDS);
    }

    public void createProcess(User user, Params params, ProcessCallbacks callbacks) {
        try {
            String paramsJson = params.toJson();
            ProcessBuilder builder = buildCommand();
            Process process = builder.start();
            OutputStream stdinStream = process.getOutputStream();
            stdinStream.write(paramsJson.getBytes());
            stdinStream.write('\n');
            stdinStream.flush();
            processes.put(process, new ProcessMeta(user, params, callbacks));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean exists(User user) {
        for (ProcessMeta meta: processes.values()) {
            if (meta.getUser().equals(user)) return true;
        }
        return false;
    }

    public void stop(User user) {
        processes.forEach((process, meta) -> {
            if (meta.getUser().equals(user)) {
                process.destroyForcibly();
            }
        });
    }

    private static ProcessBuilder buildCommand() {
        String javaHome = System.getProperty("java.home");
        String separator = System.getProperty("file.separator");
        String classPath = System.getProperty("java.class.path");
        if (classPath.trim().isEmpty()) classPath = "./*";
        List<String> command = new ArrayList<>();
        command.add(javaHome + separator + "bin" + separator + "java");
        command.add("-cp");
        command.add(classPath);
        command.add("-Xmx512M");  // Limit process RAM to 0.5GB.
        command.add(Runner.class.getName());
        return new ProcessBuilder(command);
    }

    private Runnable processWatcher() {
        return () -> {
            try {
                long timeNow = System.currentTimeMillis();
                processes.forEach((process, meta) -> {
                    if (meta == null) {
                        processes.remove(process);
                    } else if (process.isAlive()) {
                        long timePassed = timeNow - meta.getStartTime();
                        if (timePassed > maxTime) {
                            meta.getCallbacks().onFailure.accept("Process timed out.");
                            process.destroy();
                        } else {
                            try {
                                InputStream stream = process.getErrorStream();
                                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                                String text = reader.readLine().trim();
                                if (!text.isEmpty()) {
                                    int progress = Integer.parseInt(text);
                                    int max = Math.max(progress + 10, 1000);
                                    meta.getCallbacks().onProgress.accept(new Progress(progress, max));
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        int exitValue = process.exitValue();
                        processes.remove(process);
                        if (exitValue == 0) {
                            try {
                                com.chessterm.website.jiuqi.model.State state =
                                    new ObjectMapper().readValue(process.getInputStream(),
                                        com.chessterm.website.jiuqi.model.State.class);
                                meta.getCallbacks().onSuccess.accept(state);
                            } catch (IOException e) {
                                e.printStackTrace();
                                meta.getCallbacks().onFailure.accept("Unable to parse result.");
                            }
                        } else {
                            String message = "Process exited unexpectedly. (" + exitValue + ")";
                            InputStream stream = process.getErrorStream();
                            Scanner scanner = new Scanner(stream);
                            while (scanner.hasNextLine()) {
                                System.err.println(scanner.nextLine());
                            }
                            meta.getCallbacks().onFailure.accept(message);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    public static ProcessManager getInstance() {
        if (instance == null) instance = new ProcessManager();
        return instance;
    }
}
