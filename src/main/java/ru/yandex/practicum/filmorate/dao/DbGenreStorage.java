package ru.yandex.practicum.filmorate.dao;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.interfaces.GenreStorage;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class DbGenreStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DbGenreStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Genre> loadGenreById(int id) {
        String sqlQuery = "SELECT GENRE_ID, NAME FROM GENRES WHERE GENRE_ID = ?;";
        return jdbcTemplate.query(sqlQuery, new BeanPropertyRowMapper<>(Genre.class), id).stream().findAny();
    }

    @Override
    public List<Genre> loadGenresByFilmId(int id) {
        String sqlQuery =
                "SELECT g.GENRE_ID, g.NAME " +
                        "FROM FILMS_GENRES f " +
                        "JOIN GENRES g " +
                        "    ON g.GENRE_ID = f.GENRE_ID " +
                        "WHERE f.FILM_ID = ?;";
        return jdbcTemplate.query(sqlQuery, new BeanPropertyRowMapper<>(Genre.class), id);
    }

    @Override
    public void saveGenresInFilm(int filmId, List<Genre> genres) {
        List<Genre> genresDistinct = genres.stream().distinct().collect(Collectors.toList());
        jdbcTemplate.batchUpdate(
                "INSERT INTO FILMS_GENRES (FILM_ID, GENRE_ID) VALUES (?, ?);",
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement statement, int i) throws SQLException {
                        statement.setLong(1, filmId);
                        statement.setLong(2, genresDistinct.get(i).getId());
                    }

                    public int getBatchSize() {
                        return genresDistinct.size();
                    }
                });
    }

    @Override
    public void deleteGenresInFilm(int id) {
        String sqlQuery = "DELETE FROM FILMS_GENRES WHERE film_id = ?;";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public List<Genre> getAllGenres() {
        String sqlQuery = "SELECT GENRE_ID, NAME FROM GENRES;";
        return jdbcTemplate.query(sqlQuery, new BeanPropertyRowMapper<>(Genre.class));
    }
}
