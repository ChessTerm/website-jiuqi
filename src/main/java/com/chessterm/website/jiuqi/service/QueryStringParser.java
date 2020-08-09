package com.chessterm.website.jiuqi.service;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class QueryStringParser {

    public static Map<String, String> parse(String queryString) {
        Map<String, String> map = new HashMap<>();
        Arrays.stream(queryString.split("&")).forEach(param -> {
            try {
                String[] parts = param.split("=");
                map.put(urlDecode(parts[0]), urlDecode(parts[1]));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return map;
    }

    public static String urlDecode(String string) throws IOException {
        return URLDecoder.decode(string, StandardCharsets.UTF_8.toString());
    }
}
