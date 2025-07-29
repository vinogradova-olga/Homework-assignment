package com.rentacar.userservice.business.service.impl;


import com.rentacar.userservice.business.mappers.UserMapStructMapper;
import com.rentacar.userservice.business.repository.UserRepository;
import com.rentacar.userservice.business.repository.model.UserDAO;
import com.rentacar.userservice.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @Mock
    UserMapStructMapper mapper;

    private UserDAO userDAO;

    private User user;

    @BeforeEach
    public void init(){
        user = createUser();
        userDAO = createUserDAO();
    }
    private User createUser() {
        return User.builder()
                .id(1L)
                .username("John")
                .password("John123")
                .firstName("John")
                .lastName("Smith")
                .email("js@mail.com").build();
    }
    private UserDAO createUserDAO() {
        return UserDAO.builder()
                .id(1L)
                .username("John")
                .password("John123")
                .firstName("John")
                .lastName("Smith")
                .email("js@mail.com").build();
    }
    private UserDAO createUserDAO2() {
        return UserDAO.builder()
                .id(2L)
                .username("Jane")
                .password("Jane123")
                .firstName("Jane")
                .lastName("Doe")
                .email("jd@mail.com").build();
    }
    private List<UserDAO> createUserDAOList(){
        List<UserDAO> userDAOS = new ArrayList<>();
        userDAOS.add(createUserDAO());
        userDAOS.add(createUserDAO2());
        return userDAOS;
    }

    @Test
    void saveUser() {
        when(userRepository.save(userDAO)).thenReturn(userDAO);
        when(mapper.userDAOToUser(userDAO)).thenReturn(user);
        when(mapper.userToUserDAO(user)).thenReturn(userDAO);

        User userSaved = userServiceImpl.saveUser(user);

        assertEquals(user, userSaved);
        verify(userRepository, times(1)).save(userDAO);
    }

    @Test
    void findAllUsers() {
        when(userRepository.findAll()).thenReturn(createUserDAOList());
        List<User> user = userServiceImpl.findAllUsers();
        assertEquals(2,user.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void findUserById() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userDAO));
        when(mapper.userDAOToUser(userDAO)).thenReturn(user);
        Optional<User> userOptional = userServiceImpl.findUserById(user.getId());
        assertEquals(user.getId(),userOptional.get().getId());
        assertEquals(user.getFirstName(),userOptional.get().getFirstName());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void updateUser() {
        when(userRepository.save(any())).thenReturn(userDAO);
        when(mapper.userDAOToUser(userDAO)).thenReturn(user);
        when(mapper.userToUserDAO(user)).thenReturn(userDAO);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userDAO));
        Optional<User> userOptional = userServiceImpl.findUserById(user.getId());
        User updatedUser = userServiceImpl.updateUser(user);
        assertEquals(user.getId(), updatedUser.getId());
        verify(userRepository, times(1)).save(userDAO);
    }

    @Test
    void deleteUserById() {
        Long id = createUser().getId();
        userServiceImpl.deleteUserById(id);
        verify(userRepository, times(1)).deleteById(id);
    }





}