package com.chessterm.website.jiuqi.controller;

import com.chessterm.website.jiuqi.service.BoardService;
import com.chessterm.website.jiuqi.service.GameService;
import com.chessterm.website.jiuqi.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @MockBean
    UserService service;

    @MockBean
    GameService gameService;

    @MockBean
    BoardService boardService;

    @Autowired
    private MockMvc mvc;

    @Test
    public void exists_exist() throws Exception {
        long id = 10170;
        when(service.exists(id)).thenReturn(true);
        mvc.perform(get("/user/exists").param("id", String.valueOf(id))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data", is(true)));
    }

    @Test
    public void exists_notExist() throws Exception {
        mvc.perform(get("/user/exists").param("id", "0")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data", is(false)));
    }
}
