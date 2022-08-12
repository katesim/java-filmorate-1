package ru.yandex.practicum.filmorate.dao.interfaces;

import ru.yandex.practicum.filmorate.model.User;


import java.util.List;
import java.util.Optional;

public interface UserStorage {


    User createUser(User user);
    void updateUser(User user);
    List<User> getAllUsers();
    Optional<User> getUserByID(int userID);
    void addFriend(User user, User friend);
    void deleteFriend(User user, User friend);
    List<User> getFriendsByUserID(int id);

}
