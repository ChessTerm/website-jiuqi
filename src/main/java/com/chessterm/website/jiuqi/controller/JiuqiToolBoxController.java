package com.chessterm.website.jiuqi.controller;

import com.chessterm.website.jiuqi.model.Board;
import com.chessterm.website.jiuqi.model.ReturnData;
import com.chessterm.website.jiuqi.model.State;
import com.chessterm.website.jiuqi.model.User;
import com.chessterm.website.jiuqi.repository.BoardRepository;
import com.chessterm.website.jiuqi.service.mcts.Params;
import com.chessterm.website.jiuqi.service.mcts.ProcessCallbacks;
import com.chessterm.website.jiuqi.service.mcts.ProcessManager;
import com.jingbh.flamechess.jiuqi.Stage;
import com.jingbh.flamechess.util.StateParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Scanner;

@RestController
@RequestMapping("jiuqi_toolbox")
public class JiuqiToolBoxController {

    private static final int gameId = 1;

    @Autowired
    SimpMessagingTemplate template;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    BoardController boardController;

    @PostMapping("/parse_state")
    public void parseState(@RequestBody String request, @AuthenticationPrincipal User user,
                           HttpServletResponse response) {
        Board board = boardRepository.findByUserIdAndGameId(user.getId(), gameId);
        if (board != null) {
            Scanner scanner = new Scanner(request);
            State state = new State(StateParser.parse(scanner));
            boardController.updateState(board, state);
            template.convertAndSend(String.format("/topic/boards/%s/sync", board.getId()), state);
        } else response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @GetMapping("/export_state")
    public ReturnData exportState(@AuthenticationPrincipal User user) {
        Board board = boardRepository.findByUserIdAndGameId(user.getId(), gameId);
        StringBuilder result = new StringBuilder();
        if (board != null) {
            State state = board.getState();
            for (byte[] row: state.get2dState()) {
                for (byte cell: row) {
                    char cellChar;
                    switch (cell) {
                        case -1:
                            cellChar = 'X';
                            break;
                        case 1:
                            cellChar = 'O';
                            break;
                        default:
                            cellChar = '-';
                    }
                    result.append(cellChar);
                    result.append(' ');
                }
                result.append('\n');
            }
            return new ReturnData(true, "", result.toString());
        } else return new ReturnData(false, "Board not found.");
    }

    @PostMapping("/next_step/start")
    public ReturnData nextStepStart(@RequestParam(defaultValue = "0") byte player,
                              @RequestParam(defaultValue = "") Stage stage,
                              @AuthenticationPrincipal User user) {
        if (player != -1 && player != 1) {
            return new ReturnData(false, "Invalid player.");
        } else if (stage != Stage.LAYOUT && stage != Stage.PLAY) {
            return new ReturnData(false, "Invalid stage.");
        } else {
            Board board = boardRepository.findByUserIdAndGameId(user.getId(), gameId);
            if (board == null) {
                return new ReturnData(false, "Board not found.");
            } else {
                State state = board.getState();
                Params params = new Params(state, player, stage);
                String callbackDestination = String.format("/topic/next_step/%s/", user.getId());
                String syncDestination = String.format("/topic/boards/%s/sync", board.getId());
                ProcessCallbacks callbacks = new ProcessCallbacks(message -> {
                    System.out.print("Fail: ");
                    System.out.println(message);
                    template.convertAndSend(callbackDestination + "/fail", message);
                }, newState -> {
                    System.out.print("Success: ");
                    System.out.println(newState);
                    boardController.updateState(board, newState);
                    template.convertAndSend(syncDestination, newState);
                    template.convertAndSend(callbackDestination + "/success", "");
                }, progress -> {
                    System.out.print("Progress: ");
                    System.out.println(progress);
                    template.convertAndSend(callbackDestination + "/progress", progress);
                });
                ProcessManager manager = ProcessManager.getInstance();
                if (!manager.exists(user)) {
                    manager.createProcess(user, params, callbacks);
                    return new ReturnData(true);
                } else return new ReturnData(false, "Process exists.");
            }
        }
    }

    @GetMapping("/next_step/check")
    public ReturnData nextStepCheck(@AuthenticationPrincipal User user) {
        ProcessManager manager = ProcessManager.getInstance();
        return new ReturnData(true, manager.exists(user));
    }

    @PostMapping("/next_step/stop")
    public ReturnData nextStepStop(@AuthenticationPrincipal User user) {
        ProcessManager manager = ProcessManager.getInstance();
        manager.stop(user);
        return new ReturnData(true);
    }
}
