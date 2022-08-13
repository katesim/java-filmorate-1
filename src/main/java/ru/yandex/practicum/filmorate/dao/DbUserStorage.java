package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class DbUserStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DbUserStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    //Создаем юзера
    @Override
    public User createUser(User user) {
        String sqlQuery = "INSERT INTO USERS (LOGIN, NAME , EMAIL, BIRTHDAY) VALUES (?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sqlQuery, new String[]{"id"});
            statement.setString(1, user.getLogin());
            statement.setString(2, user.getName());
            statement.setString(3, user.getEmail());
            statement.setDate(4, Date.valueOf(user.getBirthday()));
            return statement;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return user;
    }

    //Обновляем юзера
    @Override
    public void updateUser(User user) {
        String sqlQuery = "UPDATE USERS SET LOGIN = ?, NAME = ?, EMAIL = ?, BIRTHDAY = ? WHERE ID = ?;";
        jdbcTemplate.update(
                sqlQuery, user.getLogin(), user.getName(), user.getEmail(), user.getBirthday(), user.getId()
        );
    }

    //Получаем список всех юзеров
    @Override
    public List<User> getAllUsers() {
        String sqlQuery = "SELECT ID, NAME, LOGIN, EMAIL, BIRTHDAY FROM USERS;";
        return jdbcTemplate.query(sqlQuery, new BeanPropertyRowMapper<>(User.class));
    }

    //Добавляем друга юзеру
    @Override
    public void addFriend(int userID, int friendID) {
        String sqlQuery = "INSERT INTO FRIENDS (USER_ID, FRIEND_ID) VALUES (?, ?);";
        jdbcTemplate.update(sqlQuery, userID, friendID);
    }

    //Удаляем друга у юзера
    @Override
    public void deleteFriend(int userID, int friendID) {
        String sqlQuery = "DELETE FROM FRIENDS WHERE USER_ID = ? AND FRIEND_ID = ?;";
        jdbcTemplate.update(sqlQuery, userID, friendID);
    }

    //Получаем юзера по ID
    @Override
    public Optional<User> getUserByID(int userID) {
        String sqlQuery = "SELECT ID, NAME, LOGIN, EMAIL, BIRTHDAY FROM USERS WHERE ID = ?;";
        return jdbcTemplate.query(sqlQuery, new BeanPropertyRowMapper<>(User.class), userID).stream().findAny();
    }

    //Получаем друзей юзера по ID юзера
    @Override
    public List<Optional<User>> getFriendsByUserID(int userID) {
        String sqlQuery = "SELECT FRIEND_ID " +
                "FROM FRIENDS " +
                "WHERE USER_ID=?";
        List<Integer> friendIds = jdbcTemplate.queryForList(sqlQuery, Integer.class, userID);
        List<Optional<User>> list = new ArrayList<>();
        for (Integer friendId : friendIds) {
            Optional<User> userById = getUserByID(friendId);
            list.add(userById);
        }
        return list;
    }

    @Override
    public List<User> getCommonFriendByUserID(int userID) {
        String sqlQuery = "SELECT ID, LOGIN, NAME, EMAIL, BIRTHDAY " +
                "FROM (" +
                "    SELECT u.ID, u.LOGIN, u.NAME, u.EMAIL, u.BIRTHDAY" +
                "    FROM FRIENDS f" +
                "    JOIN USERS u" +
                "        ON f.FRIEND_ID = u.ID" +
                "    WHERE f.USER_ID = ?" +
                "    UNION ALL" +
                "    SELECT u.ID, u.LOGIN, u.NAME, u.EMAIL, u.BIRTHDAY" +
                "    FROM FRIENDS f" +
                "    JOIN USERS u" +
                "        ON f.USER_ID = u.ID" +
                "    WHERE f.FRIEND_ID = ?" +
                ") AS FRIENDS " +
                "ORDER BY ID ASC;";
        return jdbcTemplate.query(sqlQuery, new BeanPropertyRowMapper<>(User.class), userID, userID);
    }
}

