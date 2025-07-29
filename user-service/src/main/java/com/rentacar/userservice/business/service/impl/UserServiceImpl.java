package com.rentacar.userservice.business.service.impl;

import com.rentacar.userservice.business.mappers.UserMapStructMapper;
import com.rentacar.userservice.business.repository.UserRepository;
import com.rentacar.userservice.business.repository.model.UserDAO;
import com.rentacar.userservice.business.service.UserService;
import com.rentacar.userservice.model.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private UserMapStructMapper userMapStructMapper;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setUserMapStructMapper(UserMapStructMapper userMapStructMapper) {
        this.userMapStructMapper = userMapStructMapper;
    }

    @Override
    public User saveUser(User user) {
        UserDAO userDAO = userMapStructMapper.userToUserDAO(user);
        UserDAO savedUser = userRepository.save(userDAO);
        log.info("New user is saved: {}", savedUser);
        return userMapStructMapper.userDAOToUser(userDAO);
    }

    @Override
    public Optional<User> findUserById(Long id) {
        Optional<User> userById = userRepository.findById(id)
                .flatMap(user -> Optional.ofNullable(userMapStructMapper.userDAOToUser(user)));
        log.info("User with id {} is {}", id, userById);
        return userById;
    }

    @Override
    public User updateUser(User user) {
        Long id = user.getId();

        Optional<UserDAO> optionalUserDAO = userRepository.findById(id);
        if (optionalUserDAO.isEmpty()) {
            log.error("User with id {} not found", id);
            throw new EntityNotFoundException("User not found for id: " + id);
        }
        UserDAO existingUserDAO = optionalUserDAO.get();
        UserDAO updatedUserDAO = userMapStructMapper.userToUserDAO(user);
        updatedUserDAO.setId(existingUserDAO.getId());
        UserDAO savedUserDAO = userRepository.save(updatedUserDAO);
        log.info("User with id {} updated: {}", id, savedUserDAO);
        return userMapStructMapper.userDAOToUser(savedUserDAO);
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
        log.info("User with id {} is deleted", id);
    }

    @Override
    public List<User> findAllUsers() {
        List<UserDAO> userDAOList = userRepository.findAll();
        log.info("Get user list. Size is: {}", userDAOList::size);
        return userDAOList.stream().map(userMapStructMapper::userDAOToUser).collect(Collectors.toList());
    }
}
