package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dao.film.FilmDao;
import ru.yandex.practicum.filmorate.dao.genre.GenreDao;
import ru.yandex.practicum.filmorate.dao.likes.LikesDao;
import ru.yandex.practicum.filmorate.dao.mpa.MpaDao;
import ru.yandex.practicum.filmorate.dao.user.UserDao;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
class FilmorateApplicationTests {

    private final UserDao userDao;
    private final FilmDao filmDao;
    private final GenreDao genreDao;
    private final MpaDao mpaDao;
    private final LikesDao likesDao;

    @Autowired
    public FilmorateApplicationTests(UserDao userDao, FilmDao filmDao, GenreDao genreDao,
                                     MpaDao mpaDao, LikesDao likesDao) {
        this.userDao = userDao;
        this.filmDao = filmDao;
        this.genreDao = genreDao;
        this.mpaDao = mpaDao;
        this.likesDao = likesDao;
    }

    @Test
    void filmDaoTest() {
        List<Film> films = new ArrayList<>();
        films.add(filmDao.createFilm(createFilmObject("One")));
        Film two = filmDao.createFilm(createFilmObject("Two"));
        films.add(two);
        assertThat(filmDao.findAll()).containsAll(films);
        two.setName("Updated");
        filmDao.updateFilm(two.getId(), two);
        assertThat(filmDao.findById(two.getId())).isPresent().hasValueSatisfying(film ->
                assertThat(film).hasFieldOrPropertyWithValue("id", two.getId())
                        .hasFieldOrPropertyWithValue("name", "Updated"));
        films.remove(two);
        filmDao.deleteById(two.getId());
        assertThat(filmDao.existsById(two.getId())).isFalse();
    }

    @Test
    void userDaoTest() {
        List<User> users = new ArrayList<>();
        User one = userDao.createUser(createUserObject("One"));
        users.add(one);
        User two = userDao.createUser(createUserObject("Two"));
        users.add(two);
        assertThat(userDao.findAll()).containsAll(users);
        two.setName("Updated");
        userDao.updateUser(two.getId(), two);
        assertThat(userDao.findById(two.getId())).isPresent().hasValueSatisfying(film ->
                assertThat(film).hasFieldOrPropertyWithValue("id", two.getId())
                        .hasFieldOrPropertyWithValue("name", "Updated"));
        userDao.addFriend(one.getId(), two.getId());
        assertThat(userDao.getFriends(one.getId())).hasSize(1)
                .first().hasFieldOrPropertyWithValue("name", two.getName())
                .hasFieldOrPropertyWithValue("id", two.getId());
        users.remove(two);
        userDao.deleteById(two.getId());
        assertThat(userDao.existsById(two.getId())).isFalse();
    }

    @Test
    void likeDaoTest() {
        Film film = filmDao.createFilm(createFilmObject("One"));
        Film secondFilm = filmDao.createFilm(createFilmObject("Two"));
        User user = createUserObject("TestUser");
        User createdUser = userDao.createUser(user);
        likesDao.addLike(createdUser.getId(), secondFilm.getId());
        assertThat(likesDao.getPopular(1)).hasSize(1)
                .first().hasFieldOrPropertyWithValue("id", secondFilm.getId())
                .hasFieldOrPropertyWithValue("name", "Two");
        likesDao.removeLike(createdUser.getId(), secondFilm.getId());
        assertThat(likesDao.getPopular(2)).hasSize(2)
                .first().hasFieldOrPropertyWithValue("id", film.getId())
                .hasFieldOrPropertyWithValue("name", "One");
    }

    @Test
    void getGenreById() {
        Optional<Genre> genreOptional = genreDao.findById(1L);
        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(genre ->
                        assertThat(genre).hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("name", "Комедия")
                );

    }

    @Test
    void getAllGenres() {
        assertThat(genreDao.findAll()).hasSize(6)
                .first().hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "Комедия");
    }

    @Test
    void getMpaById() {
        Optional<Mpa> mpaOptional = mpaDao.findById(1L);
        assertThat(mpaOptional)
                .isPresent()
                .hasValueSatisfying(mpa ->
                        assertThat(mpa).hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("name", "G")
                );

    }

    @Test
    void getAllMpa() {
        assertThat(mpaDao.findAll()).hasSize(5)
                .first().hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "G");
    }


    private Film createFilmObject(String name) {
        Mpa mpa = mpaDao.findById(1L).orElseThrow();
        Film film = new Film();
        film.setName(name);
        film.setReleaseDate(LocalDate.now());
        film.setMpa(mpa);
        return film;
    }

    private User createUserObject(String name) {
        User user = new User();
        user.setName(name);
        user.setLogin("Login");
        user.setEmail("test@email.user");
        user.setBirthday(LocalDate.of(1900, 1, 1));
        return user;
    }
}
