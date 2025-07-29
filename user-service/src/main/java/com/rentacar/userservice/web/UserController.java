package com.rentacar.userservice.web;

import com.rentacar.userservice.business.service.UserService;
import com.rentacar.userservice.model.User;
import com.rentacar.userservice.swagger.DescriptionVariables;
import com.rentacar.userservice.swagger.HTMLResponseMessages;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiParam;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;


import java.util.List;
import java.util.Optional;

@Api(tags = {DescriptionVariables.USER_SERVICE})
@Log4j2
@RestController
@Validated
@RequestMapping("/api/v1/user")
public class UserController {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Find the user by id",
            notes = "Provide an id to search specific user in database",
            response = User.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HTMLResponseMessages.HTTP_200),
            @ApiResponse(code = 400, message = HTMLResponseMessages.HTTP_400),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500)})
    public ResponseEntity<Optional<User>> findUserById(@ApiParam(value = "id of the user", required = true)
                                                     @NonNull @PathVariable Long id) {
        log.info("Find user by passing ID of the user, where user ID is :{} ", id);
        Optional<User> user = userService.findUserById(id);
        if (!user.isPresent()) {
            log.warn("User with id {} is not found.", id);
            return ResponseEntity.notFound().build();
        } else {
            log.debug("User with id {} is found: {}", id, user);
            return ResponseEntity.ok(user);
        }
    }
    @GetMapping
    @ApiOperation(value = "Finds all users",
            notes = "Returns the list of registered users",
            response = User.class,
            responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HTMLResponseMessages.HTTP_200),
            @ApiResponse(code = 400, message = HTMLResponseMessages.HTTP_400),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500)})
    public ResponseEntity<List<User>> findAllUsers() {
        log.info("Retrieve list of users");
        List<User> userList = userService.findAllUsers();
        if (userList.isEmpty()) {
            log.warn("User list is empty.");
            return ResponseEntity.noContent().build();
        }
        log.debug("User list size: {}", userList.size());
        return ResponseEntity.ok(userList);
    }

    @PostMapping()
    @ApiOperation(value = "Saves the user to the database",
            response = User.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = HTMLResponseMessages.HTTP_200),
            @ApiResponse(code = 400, message = HTMLResponseMessages.HTTP_400),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500)})
    @ResponseStatus(HttpStatus.CREATED)

    public ResponseEntity<User> saveUser (@Valid @RequestBody User user){
        log.info("Received values {}", user);
        User userToSave = userService.saveUser(user);
        log.info("New user is created: {}", userToSave);
        return new ResponseEntity<>(userToSave, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Updates the user by id",
            notes = "Updates the user if provided id exists",
            response = User.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = HTMLResponseMessages.HTTP_201),
            @ApiResponse(code = 400, message = HTMLResponseMessages.HTTP_400),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500)})
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<User> updateUserById(@ApiParam(value="id of the user", required = true)
                                             @NotNull @PathVariable Long id,
                                             @Valid @RequestBody User user){
        if (id <= 0) {
            log.warn("Provided user id {} is wrong", id);
            return ResponseEntity.badRequest().build();
        }
        if (!id.equals(user.getId())) {
            log.warn("User for update with id {} is not found", id);
            return ResponseEntity.notFound().build();
        }
        log.info("Update existing user with Id: {} and new body: {}", id, user);
        userService.updateUser(user);
        log.debug("User with id {} is updated: {}", id, user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Deletes the user by id",
            notes = "Deletes the user if provided id exists",
            response = User.class)
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = HTMLResponseMessages.HTTP_204_WITHOUT_DATA),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500)})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteUserById(@ApiParam(value = "The id of the user", required = true)
                                              @NotNull @PathVariable Long id) {
        if (id <= 0) {
            log.warn("Invalid user id: {}", id);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Optional<User> user = userService.findUserById(id);
        if (!user.isPresent()) {
            log.warn("User with id {} is not found", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        userService.deleteUserById(id);
        log.info("User with id {} is deleted", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
