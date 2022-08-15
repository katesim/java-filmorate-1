package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.yandex.practicum.filmorate.dao.DbFilmStorage;


import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
class FilmControllerTest {
    private final MockMvc mockMvc;
    private final DbFilmStorage filmStorage;

    @Autowired
    public FilmControllerTest(
            MockMvc mockMvc, DbFilmStorage filmStorage) {
        this.mockMvc = mockMvc;
        this.filmStorage = filmStorage;
    }

    @Test
    void createFilm() throws Exception {
        mockMvc.perform((post("/films"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"name\": \"New film\",\n" +
                                "  \"releaseDate\": \"1999-04-30\",\n" +
                                "  \"description\": \"New film about friends\",\n" +
                                "  \"duration\": 120,\n" +
                                "  \"rate\": 4,\n" +
                                "  \"mpa\": { \"id\": 3},\n" +
                                "  \"genres\": [{ \"id\": 1}]\n" +
                                "}"))
                .andExpect(status().isOk());
    }

    @Test
    void createFilmWithEmptyName() throws Exception {
        MvcResult response = mockMvc.perform((post("/films"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"name\": \"\",\n" +
                                "  \"releaseDate\": \"1999-04-30\",\n" +
                                "  \"description\": \"New film about friends\",\n" +
                                "  \"duration\": 120,\n" +
                                "  \"rate\": 4,\n" +
                                "  \"mpa\": { \"id\": 3},\n" +
                                "  \"genres\": [{ \"id\": 1}]\n" +
                                "}"))
                .andExpect(status().isBadRequest())
                .andReturn();
        String message = requireNonNull(response.getResolvedException(),
                "Не получено сообщение от контроллера").getMessage();
        assertTrue(message.contains("default message [name]"));
        assertTrue(message.contains("default message [must not be blank]"));
    }

    @Test
    void createFilmWithEmptyDescription() throws Exception {
        MvcResult response = mockMvc.perform((post("/films"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"name\": \"New film\",\n" +
                                "  \"releaseDate\": \"1999-04-30\",\n" +
                                "  \"description\": \"\",\n" +
                                "  \"duration\": 120,\n" +
                                "  \"rate\": 4,\n" +
                                "  \"mpa\": { \"id\": 3},\n" +
                                "  \"genres\": [{ \"id\": 1}]\n" +
                                "}"))
                .andExpect(status().isBadRequest())
                .andReturn();
        String message = requireNonNull(response.getResolvedException(),
                "Не получено сообщение от контроллера").getMessage();
        assertTrue(message.contains("default message [description]"));
        assertTrue(message.contains("default message [must not be blank]"));
    }

    @Test
    void createFilmWithEmptyReleaseDate() throws Exception {
        MvcResult response = mockMvc.perform((post("/films"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"name\": \"New film\",\n" +
                                "  \"releaseDate\": \"\",\n" +
                                "  \"description\": \"New film about friends\",\n" +
                                "  \"duration\": 120,\n" +
                                "  \"rate\": 4,\n" +
                                "  \"mpa\": { \"id\": 3},\n" +
                                "  \"genres\": [{ \"id\": 1}]\n" +
                                "}"))
                .andExpect(status().isBadRequest())
                .andReturn();
        String message = requireNonNull(response.getResolvedException(),
                "Не получено сообщение от контроллера").getMessage();
        assertTrue(message.contains("default message [releaseDate]"));
        assertTrue(message.contains("default message [must not be null]"));
    }

    @Test
    void createFilmWithIncorrectReleaseDate() throws Exception {
                mockMvc.perform((post("/films"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"name\": \"New film\",\n" +
                                "  \"releaseDate\": \"3088-04-30\",\n" +
                                "  \"description\": \"New film about friends\",\n" +
                                "  \"duration\": 120,\n" +
                                "  \"rate\": 4,\n" +
                                "  \"mpa\": { \"id\": 3},\n" +
                                "  \"genres\": [{ \"id\": 1}]\n" +
                                "}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createFilmWithEmptyDuration() throws Exception {
        mockMvc.perform((post("/films"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"name\": \"New film\",\n" +
                                "  \"releaseDate\": \"1999-04-30\",\n" +
                                "  \"description\": \"New film about friends\",\n" +
                                "  \"duration\": ,\n" +
                                "  \"rate\": 4,\n" +
                                "  \"mpa\": { \"id\": 3},\n" +
                                "  \"genres\": [{ \"id\": 1}]\n" +
                                "}"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void createFilmWithIncorrectDuration() throws Exception {
        mockMvc.perform((post("/films"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"name\": \"New film\",\n" +
                                "  \"releaseDate\": \"1999-04-30\",\n" +
                                "  \"description\": \"New film about friends\",\n" +
                                "  \"duration\": -1,\n" +
                                "  \"rate\": 4,\n" +
                                "  \"mpa\": { \"id\": 3},\n" +
                                "  \"genres\": [{ \"id\": 1}]\n" +
                                "}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateFilm() throws Exception {
        mockMvc.perform((post("/films"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"name\": \"New film\",\n" +
                                "  \"releaseDate\": \"1999-04-30\",\n" +
                                "  \"description\": \"New film about friends\",\n" +
                                "  \"duration\": 120,\n" +
                                "  \"rate\": 4,\n" +
                                "  \"mpa\": { \"id\": 3},\n" +
                                "  \"genres\": [{ \"id\": 1}]\n" +
                                "}"))
                .andExpect(status().isOk());

        mockMvc.perform((put("/films"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"id\": 1,\n" +
                                "  \"name\": \"Film Updated\",\n" +
                                "  \"releaseDate\": \"1989-04-17\",\n" +
                                "  \"description\": \"New film update decription\",\n" +
                                "  \"duration\": 190,\n" +
                                "  \"rate\": 4,\n" +
                                "  \"mpa\": { \"id\": 5}\n" +
                                "}"))
                .andExpect(status().isOk());
    }
    }