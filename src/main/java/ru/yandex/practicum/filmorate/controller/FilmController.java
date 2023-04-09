package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();
    private long idGenerate = 1;

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Название фильма пусто.");
            throw new ValidationException("Название фильма не может быть пустым.");
        } else if (film.getDescription().length() > 200) {
            log.error("Превышена максимальная длина описания.");
            throw new ValidationException("Максимальная длина описания — 200 символов.");
        } else if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Дата релиза фильма оказалась ранее 28 декабря 1895 года.");
            throw new ValidationException("Дата релиза фильма не может быть ранее 28 декабря 1895 года.");
        } else if (film.getDuration() < 0) {
            log.error("Продолжительность фильма отрицательная.");
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        } else {
            film.setId(idGenerate++);
            films.put(film.getId(), film);
            log.info("Добавлен фильм {}", film.getName());
        }
        return film;
    }

    @PutMapping
    public Film put(@RequestBody Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Название фильма пусто.");
            throw new ValidationException("Название фильма не может быть пустым.");
        } else if (film.getDescription().length() > 200) {
            log.error("Превышена максимальная длина описания.");
            throw new ValidationException("Максимальная длина описания — 200 символов.");
        } else if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Дата релиза фильма оказалась ранее 28 декабря 1895 года.");
            throw new ValidationException("Дата релиза фильма не может быть ранее 28 декабря 1895 года.");
        } else if (film.getDuration() < 0) {
            log.error("Продолжительность фильма отрицательная.");
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        } else if (!films.containsKey(film.getId())) {
            log.error("Нельзя обновить: фильм с id {} нет в базе данных", film.getId());
            throw new ValidationException("Фильма нет в базе данных.");
        } else {
            films.put(film.getId(), film);
            log.info("Обновлен фильм {}", film.getName());
        }
        return film;
    }
}
