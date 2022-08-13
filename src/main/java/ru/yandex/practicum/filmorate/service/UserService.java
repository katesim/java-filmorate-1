package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Service
@Slf4j
public class UserService {

    UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    //Создаем юзера
    public User createUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        User userCreated = userStorage.createUser(user);
        log.debug("Create user {}", userCreated);
        return userCreated;
    }

    //Обновляем юзера
    public User updateUser(User user) {
        User updatedUser = getUserByID(user.getId());
        if (user.getBirthday() == null) user.setBirthday(updatedUser.getBirthday());
        if (user.getLogin() == null) user.setLogin(updatedUser.getLogin());
        if (user.getEmail() == null) user.setEmail(updatedUser.getEmail());
        userStorage.updateUser(user);
        log.debug("Update user {}", user);
        return getUserByID(user.getId());
    }

    //Получаем список всех юзеров
    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    //Добавляем друга юзеру
    public void addFriend(int userID, int friendID) {
        if(friendID < 0) throw new NotFoundException("Friend not found");
        userStorage.addFriend(userID, friendID);
    }

    //Удаляем друга у юзера
    public void deleteFriend(int userID, int friendID) {
        userStorage.deleteFriend(userID, friendID);
    }

    //Получаем юзера по ID
    public User getUserByID(int userID) {
        Optional<User> user = userStorage.getUserByID(userID);
        if (user.isEmpty()) {
            throw new NotFoundException("User with ID = " + userID + " not found.");
        }
        log.debug("GET User {}",user);
        return user.get();
    }

    //Получаем список друзей по ID юзера
    public List<Optional<User>> getFriendsListForUserID(int userID) {
        List<Optional<User>> friends = userStorage.getFriendsByUserID(userID);
        log.debug("GET User friends {}", friends.size());
        return friends;
    }

    //Получаем общих друзей по ID юзеров
    public List<User> getCommonsFriendsForUsersID(int userID, int otherID) {
        List<User> friends = userStorage.getCommonFriendByUserID(userID);
        friends.retainAll(userStorage.getCommonFriendByUserID(otherID));
        log.debug("GET Common friends {}", friends.size());
        return friends;
    }
}
