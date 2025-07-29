package com.rentacar.userservice.integration;

import com.rentacar.userservice.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserServiceIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl() {
        return "http://localhost:" + port + "/api/v1/user";
    }

    @Test
    void testCreateFindDeleteUser() {
        // Create a new user
        User newUser = User.builder()
                .username("integrationUser")
                .password("pass123")
                .firstName("Integration")
                .lastName("Test")
                .email("integration@test.com")
                .build();

        ResponseEntity<User> createResponse = restTemplate.postForEntity(baseUrl(), newUser, User.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        User createdUser = createResponse.getBody();
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getId()).isNotNull();
        assertThat(createdUser.getUsername()).isEqualTo("integrationUser");

        Long userId = createdUser.getId();

        // Find the user by ID
        ResponseEntity<User> findResponse = restTemplate.getForEntity(baseUrl() + "/" + userId, User.class);
        assertThat(findResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(findResponse.getBody().getUsername()).isEqualTo("integrationUser");

        // Delete the user
        restTemplate.delete(baseUrl() + "/" + userId);

        // Verify user is deleted (expect 404)
        ResponseEntity<User> afterDeleteResponse = restTemplate.getForEntity(baseUrl() + "/" + userId, User.class);
        assertThat(afterDeleteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
