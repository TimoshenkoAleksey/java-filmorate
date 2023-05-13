CREATE TABLE IF NOT EXISTS Film (
    film_id INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar (200) NOT NULL,
    description varchar (200) NOT NULL,
    release_date date NOT NULL,
    duration int NOT NULL,
    mpa_id int NOT NULL
);

    CREATE TABLE IF NOT EXISTS Users (
    user_id INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email varchar(200) NOT NULL,
    login varchar(50) NOT NULL,
    name varchar(200) NOT NULL,
    birthday date NOT NULL,
    CONSTRAINT uc_User_Email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS Likes (
    likes_id INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id int NOT NULL,
    film_id int NOT NULL
);

CREATE TABLE IF NOT EXISTS Mpa (
    mpa_id INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar(10) NOT NULL,
    CONSTRAINT uc_Mpa_Name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS Genre (
    genre_id INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar(200) NOT NULL,
    CONSTRAINT uc_Genre_Name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS GenreFilm (
    film_id INTEGER NOT NULL REFERENCES film (film_id) ON DELETE CASCADE,
    genre_id INTEGER NOT NULL REFERENCES genre (genre_id) ON DELETE CASCADE

);

CREATE TABLE IF NOT EXISTS Friendship (
    friendship_id INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id int NOT NULL,
    friend_id int NOT NULL,
    status bool NOT NULL
);

ALTER TABLE Film ADD CONSTRAINT IF NOT EXISTS fk_Film_MpaId FOREIGN KEY(mpa_id)
REFERENCES Mpa (mpa_id) ON DELETE CASCADE;

ALTER TABLE Likes ADD CONSTRAINT IF NOT EXISTS fk_Likes_UserId FOREIGN KEY (user_id)
REFERENCES Users(user_id) ON DELETE CASCADE;

ALTER TABLE Likes ADD CONSTRAINT IF NOT EXISTS fk_Likes_FilmID FOREIGN KEY (film_id)
REFERENCES Film (film_id) ON DELETE CASCADE;

ALTER TABLE Friendship ADD CONSTRAINT IF NOT EXISTS fk_Friendship_UserId FOREIGN KEY (user_id)
REFERENCES Users(user_id) ON DELETE CASCADE;

ALTER TABLE Friendship ADD CONSTRAINT IF NOT EXISTS fk_Friendship_FriendId FOREIGN KEY (friend_id)
REFERENCES Users(user_id) ON DELETE CASCADE;

ALTER TABLE GenreFilm ADD CONSTRAINT IF NOT EXISTS fk_GenreFilm_FilmID FOREIGN KEY(film_id)
REFERENCES Film (film_id) ON DELETE CASCADE;

ALTER TABLE GenreFilm ADD CONSTRAINT IF NOT EXISTS fk_GenreFilm_GenreID FOREIGN KEY(genre_id)
REFERENCES Genre (genre_id) ON DELETE CASCADE;

ALTER TABLE GenreFilm ADD CONSTRAINT IF NOT EXISTS UC_GenreLine_GenreID_FilmID UNIQUE (genre_id, film_id);
