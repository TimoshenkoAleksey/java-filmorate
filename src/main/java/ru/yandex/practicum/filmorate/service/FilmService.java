package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
public class FilmService {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(UserStorage userStorage, FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getFilms().values();
    }

    public Film getOneFilm(String id) {
        if (!(filmStorage.getFilms().containsKey(Long.parseLong(id)))) {
            throw new NullPointerException(format("Фильма с id %s нет в базе", id));
        }
        return filmStorage.getFilms().get(Long.parseLong(id));
    }

    public Film addFilm(Film film) {
        return filmStorage.add(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.update(film);
    }

    public Film addLikes(String filmId, String userId) {
        Long idFilm = Long.parseLong((filmId));
        Long idUser = Long.parseLong(userId);
        if (!filmStorage.getFilms().containsKey(idFilm) || (!(userStorage.getUsers().containsKey(idUser)))) {
            throw new NullPointerException(format("Фильма с id %s или пользователя с id %s нет в базе", idFilm, idUser));
        } else {
            filmStorage.getFilms().get(idFilm).getLikes().add(idUser);
        }
        return filmStorage.getFilms().get(idFilm);
    }

    public Film deleteLikes(String filmId, String userId) {
        Long idFilm = Long.parseLong((filmId));
        Long idUser = Long.parseLong(userId);
        if (!(filmStorage.getFilms().containsKey(idFilm)) || (!(userStorage.getUsers().containsKey(idUser)))) {
            throw new NullPointerException(format("Фильма с id %s или пользователя с id %s нет в базе", idFilm, idUser));
        } else {
            filmStorage.getFilms().get(idFilm).getLikes().remove(idUser);
        }
        return filmStorage.getFilms().get(idFilm);
    }

    public List<Film> popular(String count) {
        int defaultCount = 10;
        if (count == null) {
            return filmStorage.getFilms().values().stream()
                    .sorted((o1, o2) -> Integer.compare(o2.getLikes().size(), o1.getLikes().size()))
                    .limit(defaultCount)
                    .collect(Collectors.toList());
        } else if (Integer.parseInt(count) <= 0) {
            throw new ValidationException("Параметр количества фильмов меньше или равны нулю: count = " + count);
        } else {
            return filmStorage.getFilms().values().stream()
                    .sorted((o1, o2) -> Integer.compare(o2.getLikes().size(), o1.getLikes().size()))
                    .limit(Integer.parseInt(count))
                    .collect(Collectors.toList());
        }
    }
}
