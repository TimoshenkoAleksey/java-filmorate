package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collection;

import static java.lang.String.format;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> getAllUsers() {
        return userStorage.getUsers().values();
    }

    public User getOneUser(String id) {
        Long userId = Long.parseLong(id);
        if (!userStorage.getUsers().containsKey(userId)) {
            throw new NullPointerException(format("Пользователя с id %s нет в базе.", userId));
        }
        return userStorage.getUsers().get(userId);
    }

    public User addUser(User user) {
        return userStorage.add(user);
    }

    public User updateUser(User user) {
        return userStorage.update(user);
    }

    public User addFriend(String userId, String friendId) {
        Long idUser = Long.parseLong(userId);
        Long idFriend = Long.parseLong(friendId);
        if (!(userStorage.getUsers().containsKey(idUser)) || !(userStorage.getUsers().containsKey(idFriend))) {
            throw new NullPointerException(format("Пользователя с id %s или %s нет в базе.", idUser, idFriend));
        } else {
            userStorage.getUsers().get(idUser).getFriends().add(idFriend);
            userStorage.getUsers().get(idFriend).getFriends().add(idUser);
        }
        return userStorage.getUsers().get(idUser);
    }

    public User deleteFriend(String userId, String friendId) {
        Long idUser = Long.parseLong(userId);
        Long idFriend = Long.parseLong(friendId);
        if (!userStorage.getUsers().containsKey(idUser) || !userStorage.getUsers().containsKey(idFriend)) {
            throw new NullPointerException(format("deleteFriend. Пользователя с id %s или %s нет в базе.", idUser, idFriend));
        } else {
            userStorage.getUsers().get(idUser).getFriends().remove(idFriend);
            userStorage.getUsers().get(idFriend).getFriends().remove(idUser);
        }
        return userStorage.getUsers().get(idUser);
    }

    public Collection<User> findFriends(String id) {
        Long userId = Long.parseLong(id);
        if (!userStorage.getUsers().containsKey(userId)) {
            throw new NullPointerException(format("findFriends. Пользователя с id %s нет в базе.", userId));
        }
        Collection<User> usersFriends = new ArrayList<>();
        for (Long friend : userStorage.getUsers().get(userId).getFriends()) {
            usersFriends.add(userStorage.getUsers().get(friend));
        }
        return usersFriends;
    }

    public Collection<User> findMutualFriends(String userId, String friendId) {
        Long idUser = Long.parseLong(userId);
        Long idFriend = Long.parseLong(friendId);
        if (!userStorage.getUsers().containsKey(idUser) || !userStorage.getUsers().containsKey(idFriend)) {
            throw new NullPointerException(format("Пользователя с id %s или %s нет в базе.", idUser, idFriend));
        }
        Collection<User> mutualFriends = new ArrayList<>();
        for (Long friend : userStorage.getUsers().get(idUser).getFriends()) {
            if (userStorage.getUsers().get(idFriend).getFriends().contains(friend)) {
                mutualFriends.add(userStorage.getUsers().get(friend));
            }
        }
        return mutualFriends;
    }

}
