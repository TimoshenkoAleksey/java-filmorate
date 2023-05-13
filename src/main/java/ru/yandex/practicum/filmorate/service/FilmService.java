package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dao.LikesDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static java.lang.String.format;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final LikesDbStorage likesDbStorage;
    private static final LocalDate FIRST_FILM_RELEASE = LocalDate.of(1895, 12, 28);


    public Collection<Film> getAllFilms() {
        log.info("На данный момент сохранено фильмов: {}", filmStorage.getAllFilms().size());
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(long filmId) {
        Film film;
        film = filmStorage.getFilmById(filmId);
        film.getGenres().addAll(filmStorage.getGenreByFilmId(filmId));
        film.getLikes().addAll(likesDbStorage.getFilmLikes(filmId));
        log.info("Получили фильм по id={}", filmId);
        return film;
    }

    public Film addFilm(Film film) {
        validationBeforeAddFilm(film);
        Film createdFilm = filmStorage.addFilm(film);
        log.info("Добавили фильм: {}", createdFilm.getName());
        return createdFilm;
    }

    public Film updateFilm(Film film) {
        validateFilmById(film.getId());
        log.info("Обновлен фильм c id = {}", film.getId());
        return filmStorage.updateFilm(film);
    }

    public void addLikes(long filmId, long userId) {
        validateFilmById(filmId);
        log.info("Пользователь id={} поставил лайк фильму id={}", userId, filmId);
        likesDbStorage.addLikeToFilm(filmId, userId);
    }

    public void deleteLikes(long filmId, long userId) {
        validateFilmById(filmId);
        validateFilmById(userId);
        log.info("Пользователь id = {} удалил лайк у фильма id = {}", userId, filmId);
        likesDbStorage.deleteLikeFromFilm(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        if (count <= 0) {
            log.error("popular: Параметр количества фильмов меньше или равен нулю.");
            throw new ValidationException("Параметр количества фильмов меньше или равен нулю: count = " + count);
        }
        return likesDbStorage.getPopularFilms(count);
    }

    private void validateFilmById(long filmId) {
        filmStorage.getFilmById(filmId);
    }

    private void validationBeforeAddFilm(Film film) {
        if (film.getReleaseDate().isBefore(FIRST_FILM_RELEASE)) {
            log.error("Дата релиза не может быть раньше {}", FIRST_FILM_RELEASE);
            throw new ValidationException(format("Дата релиза не может быть раньше %tD", FIRST_FILM_RELEASE));
        }
        List<Film> filmsFromDB = filmStorage.getAllFilms();
        for (Film film1 : filmsFromDB) {
            if (film.getName().equals(film1.getName()) && film.getReleaseDate().equals(film1.getReleaseDate())
                    && film.getDuration() == film1.getDuration()) {
                log.error("Фильм с name={}, releaseDate={}, duration={}, уже существует", film.getName(),
                        film.getReleaseDate(), film.getDuration());
                throw new ValidationException("Фильм с name=" + film.getName() + ", releaseDate=" +
                        film.getReleaseDate() + ", duration= " + film.getDuration() + ", уже существует");
            }
        }
    }
}
