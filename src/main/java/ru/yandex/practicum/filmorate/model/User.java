package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
public class User {

    private int id;

    @Email
    @NotBlank
    @NotNull
    private String email;

    @NotBlank
    @NotNull
    private String login;

    private String name;

    @PastOrPresent
    private LocalDate birthday;

    @JsonIgnore
    Set<Integer> friends = new HashSet<>();

    @JsonIgnore
    Map<User,Boolean> friendshipStatus = new HashMap<>();
}