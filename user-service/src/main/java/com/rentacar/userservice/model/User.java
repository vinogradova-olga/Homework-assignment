package com.rentacar.userservice.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@ApiModel(description = "Model of user data")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class User {

    @ApiModelProperty(notes = "The unique id of the user")
    private Long id;

    @ApiModelProperty(notes = "Username of the client to log in the car booking service")
    @NotNull
    @Size(min = 3, max = 20)
    private String username;

    @ApiModelProperty(notes = "Username of the client to log in the car booking service")
    @NotNull
    @Size(min = 7, max = 20)
    private String password;

    @ApiModelProperty(notes = "Client's first name")
    @NotNull
    @Size(min=2, max=30)
    private String firstName;

    @ApiModelProperty(notes = "Client's last name")
    @NotNull
    @Size(min=2, max=30)
    private String lastName;

    @ApiModelProperty(notes = "Client's email")
    @Email
    private String email;
}
