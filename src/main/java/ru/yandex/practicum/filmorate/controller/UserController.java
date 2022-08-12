package ru.yandex.practicum.filmorate.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;

import java.util.List;


@RestController
public class UserController {

    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    //Создаем юзера
    @PostMapping(value = "/users")
    public User create(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    //Обновляем юзера
    @PutMapping(value = "/users")
    public User update(@Valid @RequestBody User user){
        return userService.updateUser(user);
    }

    //Получаем список всех юзеров
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    //Добавляем друга юзеру
    @PutMapping(value = "/users/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.addFriend(id, friendId);
    }

    //Удаляем друга у юзера
    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.deleteFriend(id, friendId);
    }

    //Получаем юзера по ID
    @GetMapping("/users/{userId}")
    public User getUserByID(@PathVariable int userId) {
        return userService.getUserByID(userId);
    }

    //Получаем друзей юзера по ID юзера
    @GetMapping("/users/{id}/friends")
    public List<User> getFriendsListForUserID(@PathVariable int id) {
        return userService.getFriendsListForUserID(id);
    }

    //Получаем общих друзей по ID юзеров
    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getCommonsFriendsForUsersID(@PathVariable int id, @PathVariable int otherId) {
        return userService.getCommonsFriendsForUsersID(id,otherId);
    }

}