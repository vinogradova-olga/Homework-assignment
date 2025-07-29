package com.rentacar.userservice.web;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.rentacar.userservice.business.service.UserService;
import com.rentacar.userservice.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    public static String baseUrl = "/api/v1/user";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService userService;

    private User user;

    @BeforeEach
    public void init() {
        user = userToTest();
    }

    private User userToTest() {
        return User.builder()
                .id(1L)
                .username("John")
                .password("John123")
                .firstName("John")
                .lastName("Smith")
                .email("js@mail.com").build();
    }

    @Test
    void findUserById_success() throws Exception {
        Mockito.when(userService.findUserById(user.getId())).thenReturn(Optional.of(user));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(baseUrl+ "/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.firstName", is("John")));
    }

    @Test
    void findUserById_notFound() throws Exception{
        Long id = 5L;
        when(userService.findUserById(id)).thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders
                        .get(baseUrl + "/5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAllUsers_success() throws Exception{
        User secondUser = User.builder()
                .id(2L)
                .username("Jane")
                .password("Jane123")
                .firstName("Jane")
                .lastName("Doe")
                .email("jd@mail.com").build();


        List<User> userList = new ArrayList<>(Arrays.asList(user,secondUser));
        Mockito.when(userService.findAllUsers()).thenReturn(userList);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(baseUrl)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName",is("John")))
                .andExpect(jsonPath("$[1].firstName",is("Jane")));
    }

    @Test
    void findAllUsers_noContent() throws Exception {
        List<User> emptyList = new ArrayList<>();
        Mockito.when(userService.findAllUsers()).thenReturn(emptyList);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(baseUrl)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void saveUser_success() throws Exception {
        Mockito.when(userService.saveUser(user)).thenReturn(user);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user));

        mockMvc.perform(mockRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName",is("John")));
    }

    @Test
    void updateUserById_success() throws Exception {
        User updatedUser = User.builder()
                .id(1L)
                .username("John")
                .password("John123")
                .firstName("John")
                .lastName("Smith")
                .email("js123@mail.com").build();


        when(userService.findUserById(user.getId())).thenReturn(Optional.of(user));
        when(userService.updateUser(updatedUser)).thenReturn(updatedUser);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .put(baseUrl + "/{id}", user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updatedUser));

        mockMvc.perform(mockRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email", is("js123@mail.com")));

        verify(userService, times(1)).updateUser(updatedUser);
    }

    @Test
    public void updateUserById_invalidId() throws Exception {
        Long invalidId = 0L;
        mockMvc.perform(put(baseUrl + "/{id}", invalidId))
                .andExpect(status().isBadRequest());
        verify(userService, times(0)).updateUser(null);
    }

    @Test
    public void updateUserById_notFound() throws Exception {
        Long id = 7L;
        when(userService.findUserById(id)).thenReturn(Optional.empty());
        this.mockMvc.perform(MockMvcRequestBuilders
                        .put(baseUrl + "/7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(userService, times(0)).updateUser(user);
    }

    @Test
    void deleteUserById_success() throws Exception{
        Long id = user.getId();
        when(userService.findUserById(id)).thenReturn(Optional.of(user));
        mockMvc.perform(delete(baseUrl + "/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUserById_notExistingId() throws Exception{
        Long id = user.getId();
        when(userService.findUserById(id)).thenReturn(Optional.empty());
        mockMvc.perform(delete(baseUrl + "/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUserById_invalidId() throws Exception{
        Long id = 0L;
        mockMvc.perform(delete(baseUrl + "/{id}", id))
                .andExpect(status().isBadRequest());
    }
}