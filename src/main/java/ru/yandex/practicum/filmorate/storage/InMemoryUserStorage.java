package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private long idGenerator = 1;

    @Override
    public User add(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения {}", user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем.");
        } else {
            user.setId(idGenerator++);
            users.put(user.getId(), user);
            log.info("Добавлен пользователь {}", user.getName());
        }
        return user;
    }

    @Override
    public User update(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения {}", user.getBirthday());
            throw new NullPointerException("Дата рождения не может быть в будущем.");
        } else if (!users.containsKey(user.getId())) {
            log.error("Нельзя обновить: пользователя с id {} нет в базе данных", user.getId());
            throw new NullPointerException("Пользователя нет в базе данных.");
        } else {
            users.put(user.getId(), user);
            log.info("Обновлен пользователь {}", user.getName());
        }
        return user;
    }

    @Override
    public User delete(String id) {
        Long userId = Long.parseLong(id);
        if (!users.containsKey(userId)) {
            log.error("Нельзя удалить: пользователя с id {} нет в базе данных", userId);
            throw new NullPointerException("Пользователя нет в базе данных.");
        } else {
            log.info("Удален пользователь с id: {}", userId);
            return users.remove(userId);
        }
    }

    @Override
    public Map<Long, User> getUsers() {
        return users;
    }
}
