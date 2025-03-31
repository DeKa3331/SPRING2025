package com.umcsuser.carrent.repositories.impl;

import com.google.gson.reflect.TypeToken;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.repositories.UserRepository;
import com.umcsuser.carrent.utils.JsonFileStorage;

import java.util.*;
import java.util.stream.Collectors;

public class UserJsonRepository implements UserRepository {
    private final JsonFileStorage<User> storage =
            new JsonFileStorage<>("users.json", new TypeToken<List<User>>(){}.getType());
    private final List<User> users;

    public UserJsonRepository() {
        this.users = new ArrayList<>(storage.load());
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users);
    }

    @Override
    public Optional<User> findById(String id) {
        return users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst();
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return users.stream()
                .filter(u -> u.getLogin().equals(login))
                .findFirst();
    }

    @Override
    public User save(User user) {
        if (user.getId() == null || user.getId().isBlank()) {
            user.setId(UUID.randomUUID().toString());
        } else {
            deleteById(user.getId());
        }
        users.add(user);
        storage.save(users);
        return user;
    }

    @Override
    public void deleteById(String id) {
        users.removeIf(u -> u.getId().equals(id));
        storage.save(users);
    }
}