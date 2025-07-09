package com.umcsuser.bookstore.service;


import com.umcsuser.bookstore.dto.UserRequest;
import com.umcsuser.bookstore.models.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void register(UserRequest req);
    Optional<User> findByLogin(String login);
    void softDeleteUser(String id);
    List<User> getAllUsers();
    void addRoleToUser(String id, String roleName);
    void removeRoleFromUser(String id, String roleName);
}
