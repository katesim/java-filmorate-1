package ru.yandex.practicum.filmorate.dao;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.interfaces.GenreStorage;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class DbGenreStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DbGenreStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Genre> loadGenreById(int id) {
        String sqlQuery = "SELECT ID, NAME FROM GENRES WHERE ID = ?;";
        return jdbcTemplate.query(sqlQuery, new BeanPropertyRowMapper<>(Genre.class), id).stream().findAny();
    }

    @Override
    public List<Genre> loadGenresByFilmId(int id) {
        String sqlQuery =
                "SELECT distinct g.ID, g.NAME " +
                        "FROM FILMS_GENRES f " +
                        "JOIN GENRES g " +
                        "    ON g.ID = f.GENRE_ID " +
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
                        statement.setInt(1, filmId);
                        statement.setInt(2, genresDistinct.get(i).getId());
                    }
                    public int getBatchSize() {
                        return genresDistinct.size();
                    }
                });


    }

    @Override
    public List<Genre> findByIds(List<Integer> ids) {
        return jdbcTemplate.query(getSqlFindByIds(ids.size()), this::mapRowToObject, ids.toArray());
    }


    @Override
    public void deleteGenresInFilm(int id) {
        String sqlQuery = "DELETE FROM FILMS_GENRES WHERE FILM_ID = ?;";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public List<Genre> getAllGenres() {
        String sqlQuery = "SELECT ID, NAME FROM GENRES;";
        return jdbcTemplate.query(sqlQuery, new BeanPropertyRowMapper<>(Genre.class));
    }

    private String getSqlFindByIds(int idsLength) {
        String sqlCondition = String.join(" ,", Collections.nCopies(idsLength, "?"));
        return String.format("SELECT g.ID, g.NAME\n" +
                "FROM GENRES g\n" +
                "WHERE g.ID in (%s)", sqlCondition);
    }


    private Genre mapRowToObject(ResultSet resultSet, int row) throws SQLException {
        Genre genre = new Genre();
        genre.setId(resultSet.getInt("ID"));
        genre.setName(resultSet.getString("NAME"));
        return genre;
    }
}
