package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class DbFilmStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreService genreService;

    @Autowired
    public DbFilmStorage(JdbcTemplate jdbcTemplate, GenreService genreService){
        this.jdbcTemplate = jdbcTemplate;
        this.genreService = genreService;
    }


    @Override
    public Optional<Film> createFilm(Film film) {
        String sqlQuery = "INSERT INTO films (NAME, DESCRIPTION, RELEASE_DATE,  DURATION, MPA_ID) " +
                "VALUES (?, ?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery, new String[]{"id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setLong(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        if (film.getId() > 1) {
            saveGenresInFilm(film,keyHolder);
        }
        return Optional.of(film);
    }

    @Override
    public Optional<Film> saveGenresInFilm(Film filmData,KeyHolder keyHolder) {
        Optional<Film> optionalFilm = getFilmByID(Objects.requireNonNull(keyHolder.getKey()).intValue());
        optionalFilm.ifPresent(film -> {
            if (!film.getGenres().isEmpty()) {
                genreService.deleteGenresInFilm(filmData.getId());
            }
        });
        String sqlQuery = "INSERT INTO FILMS_GENRES (GENRE_ID, FILM_ID)" +
                "VALUES (?, ?)";
        filmData.getGenres().forEach(genre -> {
            try {
                jdbcTemplate.update(sqlQuery, genre.getId(), filmData.getId());
            } catch (DuplicateKeyException e) {
                e.getMessage();
            }
        });
        if (filmData.getGenres() != null && filmData.getGenres().size() != 0) {
            optionalFilm.ifPresent(film -> {
                film.setGenres(genreService.findByIds(filmData.getGenres().stream().mapToInt(Genre::getId).boxed().collect(Collectors.toList())));
            });
        } else {
            optionalFilm.ifPresent(film -> film.setGenres(new ArrayList<>()));
        }
        return optionalFilm;
    }

    @Override
    public void updateFilm(Film film) {
        String sqlQuery = "UPDATE films " +
                "SET NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, MPA_ID = ? " +
                "WHERE ID = ?;";
        jdbcTemplate.update(
                sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
    }

    @Override
    public List<Film> getAllFilms() {
        String sqlQuery =
                "SELECT f.ID, " +
                        "f.NAME, " +
                        "f.DESCRIPTION, " +
                        "f.RELEASE_DATE, " +
                        "f.DURATION, " +
                        "f.MPA_ID, " +
                        "m.NAME mpa, " +
                        "FROM FILMS f " +
                        "JOIN MPA m" +
                        "    ON m.ID = f.MPA_ID;";
        return jdbcTemplate.query(sqlQuery, new FilmMapper(genreService));
    }

    //???????????????? ?????????? ???? ID
    @Override
    public Optional<Film> getFilmByID(int filmID) {
        String sqlQuery =
                "SELECT f.ID, " +
                        "f.NAME, " +
                        "f.DESCRIPTION, " +
                        "f.RELEASE_DATE, " +
                        "f.DURATION, " +
                        "f.MPA_ID, " +
                        "m.NAME mpa, " +
                        "FROM FILMS f " +
                        "JOIN MPA m" +
                        "    ON m.ID = f.MPA_ID " +
                        "WHERE f.ID = ?;";
        return jdbcTemplate.query(sqlQuery, new FilmMapper(genreService), filmID).stream().findAny();
    }

    //?????????????????? ???????? ????????????
    @Override
    public void addLike(int filmID, int userID) {
        String sqlQuery = "INSERT INTO RATING_FILMS (FILM_ID, USER_ID) VALUES (?, ?);";
        jdbcTemplate.update(sqlQuery, filmID, userID);
    }

    //?????????????? ???????? ?? ????????????
    @Override
    public void deleteLike(int filmID, int userID) {
        String sqlQuery = "DELETE FROM RATING_FILMS WHERE FILM_ID = ? AND USER_ID = ?;";
        jdbcTemplate.update(sqlQuery, filmID, userID);
    }

    // ???????????????? ???????? ???? ???????? ???? ???????????????????????? ????????????
    @Override
    public boolean checkedLikeFromUser(int filmID, int userID) {
        String sqlQuery = "SELECT COUNT(USER_ID) FROM RATING_FILMS WHERE FILM_ID = ? AND USER_ID = ?;";
        int like = jdbcTemplate.queryForObject(sqlQuery, Integer.class, filmID, userID);
        return like != 0;
    }
    // ???????????????? 10 ???????????????????? ??????????????
    @Override
    public List<Film> getPopularFilms(long count) {
        String sqlQuery =
                "SELECT f.ID, " +
                        "f.NAME, " +
                        "f.DESCRIPTION, " +
                        "f.RELEASE_DATE, " +
                        "f.DURATION, " +
                        "f.MPA_ID, " +
                        "m.NAME mpa, " +
                        "FROM FILMS f " +
                        "JOIN MPA m" +
                        "    ON m.ID = f.MPA_ID " +
                        "LEFT JOIN (SELECT FILM_ID, " +
                        "      COUNT(USER_ID) rating " +
                        "      FROM RATING_FILMS " +
                        "      GROUP BY FILM_ID " +
                        ") r ON f.ID =  r.FILM_ID " +
                        "ORDER BY r.rating DESC " +
                        "LIMIT ?;";
        return jdbcTemplate.query(sqlQuery, new FilmMapper(genreService), count);
    }
}
