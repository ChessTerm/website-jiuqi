package com.chessterm.website.jiuqi.controller;

import com.chessterm.website.jiuqi.model.Board;
import com.chessterm.website.jiuqi.model.State;
import com.chessterm.website.jiuqi.model.User;
import com.chessterm.website.jiuqi.repository.BoardRepository;
import com.jingbh.flamechess.util.StateParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
