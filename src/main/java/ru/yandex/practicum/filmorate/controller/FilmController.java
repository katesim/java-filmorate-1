package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@RestController
public class FilmController {
    FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    //Создаем фильм
    @PostMapping(value = "/films")
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmService.createFilm(film);
    }

    //Обновляем фильм
    @PutMapping(value = "/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    //Получаем список всех фильмов
    @GetMapping("/films")
    public List<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    //Получаем фильм по ID
    @GetMapping("/films/{filmId}")
    public Film getFilmForID(@PathVariable int filmId) {
        return filmService.getFilmByID(filmId);
    }

    //Добавляем лайк фильму
    @PutMapping(value = "/films/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        filmService.addLike(id, userId);
    }

    //Удаляем лайк у фильма
    @DeleteMapping("/films/{id}/like/{userId}")
    public void deleteLike(@PathVariable int id, @PathVariable int userId) {
        filmService.deleteLike(id, userId);
    }

    // Получаем 10 популярных фильмов
    @GetMapping("/films/popular")
    public List<Film> getTenPopularFilms(@RequestParam(required = false, defaultValue = "10") int count) {
        return filmService.getTenPopularFilms(count);
    }
}