package ru.yandex.practicum.filmorate.dao.interfaces;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FilmStorage {

    Film createFilm(Film film);
    void updateFilm(Film film);
    List<Film> getAllFilms();
    Optional<Film> getFilmByID(int filmID);
    void addLike(int filmID, int userID);
    void deleteLike(int filmID, int userID);
    List<Film> getPopularFilms(long count);

    boolean checkedLikeFromUser(int filmID, int userID);
}
