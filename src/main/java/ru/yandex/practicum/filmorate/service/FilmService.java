package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.dao.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;


import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private final UserService userService;
    private final FilmStorage filmStorage;
    private final GenreService genreService;

    @Autowired
    public FilmService(UserService userService, FilmStorage filmStorage, GenreService genreService) {
        this.userService = userService;
        this.filmStorage = filmStorage;
        this.genreService = genreService;
    }


    //Создаем фильм
    public Film createFilm(Film film) {
        Film film1 = filmStorage.createFilm(film);
        if (film.getGenres() != null && film.getGenres().size() > 0) {
            genreService.saveGenresInFilm(film1.getId(), film.getGenres());
        }
        Film createdFilm = getFilmByID(film.getId());
        log.debug("POST Create film {}", createdFilm);
        return createdFilm;
    }

    //Обновляем фильм
    public Film updateFilm(Film film) {
        Film updatingFilm = getFilmByID(film.getId());
        if (film.getDescription() == null) film.setDescription(updatingFilm.getDescription());
        if (film.getReleaseDate() == null) film.setReleaseDate(updatingFilm.getReleaseDate());
        if (film.getDuration() == 0L) film.setDuration(updatingFilm.getDuration());
        if (film.getName() == null || film.getName().isBlank()) film.setName(updatingFilm.getName());
        if (film.getMpa() == null) film.setMpa(updatingFilm.getMpa());
        if (film.getGenres() == null) {
            film.setGenres(updatingFilm.getGenres());
        } else if (film.getGenres().size() == 0) {
            genreService.deleteGenresInFilm(film.getId());
        } else {
            genreService.updateFilmGenres(film.getId(), film.getGenres());
        }
        filmStorage.updateFilm(film);
        Film savedFilm = getFilmByID(film.getId());
        log.debug("Update {}", savedFilm);
        return savedFilm;
    }

    //Получаем список всех фильмов
    public List<Film> getAllFilms() {
        List<Film> films = filmStorage.getAllFilms();
        log.debug("GET films {}", films.size());
        return films;
    }

    //Получаем фильм по ID
    public Film getFilmByID(int filmId) {
        Optional<Film> film = filmStorage.getFilmByID(filmId);
        if (film.isEmpty()) {
            throw new NotFoundException("Film with ID = " + filmId + " not found.");
        }
        log.debug("GET film {}", film.get());
        return film.get();
    }

    //Добавляем лайк фильму
    public void addLike(int filmID, int userID) {
        userService.getUserByID(userID);
        getFilmByID(filmID);
        if (filmStorage.checkedLikeFromUser(filmID, userID)) {
            log.debug("User don't liked this film {}, user {}", filmID, userID);
        } else {
            filmStorage.addLike(filmID, userID);
            log.debug("Add like movie {} by user {}", filmID, userID);
        }
    }

    //Удаляем лайк у фильма
    public void deleteLike(int filmID,int userID){
        userService.getUserByID(userID);
        getFilmByID(filmID);
        if (filmStorage.checkedLikeFromUser(filmID, userID)) {
            filmStorage.deleteLike(filmID, userID);
            log.debug("Delete like movie {} by user {}", filmID, userID);
        } else {
            log.debug("User don't liked this film {}, user {}", filmID, userID);
        }
    }

    // Получаем 10 популярных фильмов
    public List<Film> getTenPopularFilms ( int count){
        List<Film> popularFilms = filmStorage.getPopularFilms(count);
        log.debug("GET popular films {}", popularFilms.size());
        return popularFilms;
    }
}

