package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dao.FriendshipDbStorage;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;
    private final FriendshipDbStorage friendshipDbStorage;

    public Collection<User> getAllUsers() {
        log.info("На данный момент сохранено пользователей: {}", userStorage.getUsers().size());
        List<User> allUsers = userStorage.getUsers();
        for (User user : allUsers) {
            Set<Long> usersFriends = user.getFriends();
            usersFriends.addAll(friendshipDbStorage.getAllFriendsById(user.getId()).stream().map(User::getId)
                    .collect(Collectors.toSet()));
        }
        return allUsers;
    }

    public User getUserById(long id) {
        User user = userStorage.getUserById(id);
        log.info("Нашли пользователя с id = {}", id);
        Set<Long> usersFriends = user.getFriends();
        usersFriends.addAll(friendshipDbStorage.getAllFriendsById(user.getId()).stream().map(User::getId)
                .collect(Collectors.toSet()));
        return user;
    }

    public User addUser(User user) {
        validateBeforeAdd(user);
        Set<Long> usersFriends = user.getFriends();
        for (Long friendId : usersFriends) {
            if (!userStorage.getUsers().contains(userStorage.getUserById(friendId))) {
                log.error("Пользователя с id = {} еще не существует", friendId);
                usersFriends.remove(friendId);
                throw new ValidationException(format("Пользователя с id = %s еще не существует", friendId));
            }
        }
        User createdUser = userStorage.addUser(user);
        Set<Long> friends = user.getFriends();
        for (Long friendId : friends) {
            addFriend(createdUser.getId(), friendId);
        }
        log.info("Добавили пользователя: {}", createdUser);
        return createdUser;
    }

    public User updateUser(User user) {
        userStorage.getUserById(user.getId());
        Set<Long> userFriends = user.getFriends();
        userFriends.forEach(friendsId -> addFriend(user.getId(), friendsId));
        log.info("Обновили пользователя с id = {}", user.getId());
        return userStorage.updateUser(user);
    }

    public void addFriend(long userId, long friendId) {
        if (userId == friendId) {
            log.error("Себя в друзья к себе добавить нельзя");
            throw new ValidationException("Себя в друзья к себе добавить нельзя, к сожаленью :)");
        }
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        Set<Long> usersFriends = user.getFriends();
        Set<Long> friendsFriends = friend.getFriends();
        boolean isUserHasFriend = usersFriends.contains(friendId);
        boolean isFriendHasUser = friendsFriends.contains(userId);
        if (!isUserHasFriend && !isFriendHasUser) {
            friendshipDbStorage.addFriend(userId, friendId);
            usersFriends.add(friendId);
            log.info("Пользователь id = {} добавил в друзья пользователя id = {}", userId, friendId);
        } else if (!isUserHasFriend) {
            friendshipDbStorage.addFriend(userId, friendId);
            friendshipDbStorage.updateFriendship(userId, friendId, true);
            friendshipDbStorage.updateFriendship(friendId, userId, true);
            log.info("Пользователь id = {} подтвердил дружбу с пользователем id = {}", userId, friendId);
            usersFriends.add(friendId);
        } else {
            log.info("Пользователь id = {} уже в друзьях у пользователя id = {}", friendId, userId);
            throw new ValidationException(format("Пользователь id = %s уже в друзьях у пользователя id = %s",
                    friendId, userId));
        }
    }

    public void deleteFriend(long userId, long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        Set<Long> usersFriends = user.getFriends();
        Set<Long> friendsFriends = friend.getFriends();
        if (!usersFriends.contains(friendId)) {
            log.error("Пользователь id = {} не в друзьях у пользователя id = {}", friendId, userId);
            throw new ValidationException(format("Пользователь id = %s не в друзьях у пользователя id = %s",
                    friendId, userId));
        } else if (!friendsFriends.contains(userId)) {
            friendshipDbStorage.deleteFriend(userId, friendId);
            log.info("Пользователь id = {} удалил из друзей пользователя id = {}", userId, friendId);
        } else {
            friendshipDbStorage.deleteFriend(userId, friendId);
            friendshipDbStorage.updateFriendship(friendId, userId, false);
            log.info("Пользователь id = {} удалил из друзей пользователя id = {}, статус дружбы обновлен",
                    userId, friendId);
        }
    }

    public Collection<User> getFriendsById(long userId) {
        return friendshipDbStorage.getAllFriendsById(userId);
    }

    public Collection<User> getCommonFriends(long userId, long friendId) {
        return friendshipDbStorage.getCommonFriends(userId, friendId);
    }

    private void validateBeforeAdd(User user) {
        if (user.getLogin().contains(" ")) {
            log.error("Логин не может содержать пробелы");
            throw new ValidationException("Логин не может содержать пробелы");
        }
        if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
        }
        List<User> usersFromDB = userStorage.getUsers();
        for (User user1 : usersFromDB) {
            if (user1.getEmail().equals(user.getEmail())) {
                log.error("Пользователь с email = {} уже существует", user.getEmail());
                throw new ValidationException(format("Пользователь с email = %s уже существует", user.getEmail()));
            }
            if (user1.getLogin().equals(user.getLogin())) {
                log.error("Пользователь с login = {} уже существует", user.getLogin());
                throw new ValidationException(format("Пользователь с login = %s уже существует", user.getLogin()));
            }
        }
    }
}
