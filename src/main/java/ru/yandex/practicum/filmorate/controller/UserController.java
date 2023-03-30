package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();
    private int idGenerator = 1;

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if(user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("Электронная почта пуста или не содержать символ @");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @.");
        } else if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error("Логин пустой или содержит пробелы");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы.");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения {}", user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем.");
        } else {
            user.setId(idGenerator++);
            users.put(user.getId(), user);
            log.info("Добавлен пользователь {}", user.getName());
        }
        return user;
    }

    @PutMapping
    public User put(@RequestBody User user) {
        if(user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("Электронная почта пуста или не содержать символ @");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @.");
        } else if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error("Логин пустой или содержит пробелы");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы.");
        } else if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения {}", user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем.");
        } else if (!users.containsKey(user.getId())) {
            log.error("Нельзя обновить: пользователя с id {} нет в базе данных", user.getId());
            throw new ValidationException("Пользователя нет в базе данных.");
        } else {
            users.put(user.getId(), user);
            log.info("Обновлен пользователь {}", user.getName());
        }
        return user;
    }
}
