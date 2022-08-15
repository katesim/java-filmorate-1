package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.interfaces.MpaStorage;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

@Repository
public class DbMpaStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DbMpaStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Mpa> getMpaById(int id) {
        String sqlQuery = "SELECT ID, NAME FROM MPA WHERE ID = ?;";
        return jdbcTemplate.query(sqlQuery, new BeanPropertyRowMapper<>(Mpa.class), id).stream().findFirst();
    }

    @Override
    public List<Mpa> getAllMpa() {
        String sqlQuery = "SELECT ID, NAME FROM MPA;";
        return jdbcTemplate.query(sqlQuery, new BeanPropertyRowMapper<>(Mpa.class));
    }
}
