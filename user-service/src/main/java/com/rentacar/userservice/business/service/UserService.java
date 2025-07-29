package com.rentacar.userservice.business.service;

import com.rentacar.userservice.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User saveUser(User user);

    Optional<User> findUserById(Long id);

    User updateUser(User user);

    void deleteUserById(Long id);

    List<User> findAllUsers();
}
