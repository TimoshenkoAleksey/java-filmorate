package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private long idGenerate = 1;

    @Override
    public Film add(Film film) {
        film.setId(idGenerate++);
        films.put(film.getId(), film);
        log.info("Добавлен фильм {}", film.getName());
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            log.error("Нельзя обновить: фильма с id {} нет в базе данных", film.getId());
            throw new NullPointerException("Фильма нет в базе данных.");
        } else {
            films.put(film.getId(), film);
            log.info("Обновлен фильм {}", film.getName());
        }
        return film;
    }

    @Override
    public Film delete(String id) {
        Long filmId = Long.parseLong(id);
        if (!films.containsKey(filmId)) {
            log.error("Нельзя удалить: фильма с id {} нет в базе данных", filmId);
            throw new NullPointerException("Фильма нет в базе данных.");
        }
        return films.remove(filmId);
    }

    @Override
    public Map<Long, Film> getFilms() {
        return films;
    }
}
