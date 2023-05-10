package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.GenreDbStorage;

import java.util.List;

import static java.lang.String.format;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenreService {

    private final GenreDbStorage genreDbStorage;

    public List<Genre> getAllGenres() {
        log.info("Получен список жанров");
        return genreDbStorage.getAllGenres();
    }

    public Genre getGenreById(int genreId) {
        try {
            return genreDbStorage.getGenreById(genreId);
        } catch (EmptyResultDataAccessException e) {
            log.debug("Жанр с id = {} не найден", genreId);
            throw new NullPointerException(format("Жанр с id = %s не найден", genreId));
        }
    }
}
