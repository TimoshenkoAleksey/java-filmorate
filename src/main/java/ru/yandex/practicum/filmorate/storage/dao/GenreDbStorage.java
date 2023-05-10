package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class GenreDbStorage {

    private final JdbcTemplate jdbcTemplate;

    public List<Genre> getAllGenres() {
        String sql = "SELECT * FROM Genre ORDER BY genre_id";
        return jdbcTemplate.query(sql, new GenreMapper());
    }

    public Genre getGenreById(int genreId) {
        String sql = "SELECT * FROM Genre WHERE genre_id = ?";
        return jdbcTemplate.queryForObject(sql, new GenreMapper(), genreId);
    }
}
