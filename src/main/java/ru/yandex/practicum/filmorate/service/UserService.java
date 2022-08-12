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
        User user = getUserByID(userID);
        User friend = getUserByID(friendID);
        if (user == null) {
            throw new NotFoundException("User with ID = " + userID + " not found.");
        }
        if (friend == null) {
            throw new NotFoundException("Friend with ID = " + friendID + " not found.");
        }
        userStorage.addFriend(user, friend);
    }

    //Удаляем друга у юзера
    public void deleteFriend(int userID, int friendID) {
        User user = getUserByID(userID);
        User friend = getUserByID(friendID);
        if (user == null) {
            throw new NotFoundException("User with ID = " + userID + " not found.");
        }
        if (friend == null) {
            throw new NotFoundException("Friend with ID = " + friendID + " not found.");
        }
        if (!userStorage.getAllUsers().contains(user) || !userStorage.getAllUsers().contains(friend)) {
            throw new NotFoundException("User or friend with ID not found.");
        }
        userStorage.deleteFriend(user, friend);
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
    public List<User> getFriendsListForUserID(int userID) {
        getUserByID(userID);
        List<User> friends = userStorage.getFriendsByUserID(userID);
        log.debug("GET User friends {}", friends.size());
        return friends;
    }

    //Получаем общих друзей по ID юзеров
    public List<User> getCommonsFriendsForUsersID(int userID, int otherID) {
        List<User> friends = getFriendsListForUserID(userID);
        friends.retainAll(getFriendsListForUserID(otherID));
        log.debug("GET Common friends {}", friends.size());
        return friends;
    }
}
