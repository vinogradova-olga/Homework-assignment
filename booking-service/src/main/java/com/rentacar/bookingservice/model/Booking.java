package com.rentacar.bookingservice.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@ApiModel(description = "Model of booking data")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Booking {

    @ApiModelProperty(notes = "The unique id of the booking")
    private Long id;

    @ApiModelProperty(notes = "The unique id of the user")
    private Long userId;

    @ApiModelProperty(notes = "The unique id of the car")
    private Long carId;

    @ApiModelProperty(notes = "Date when customer receives the car")
    @NotNull
    private LocalDate pickUpDate;

    @ApiModelProperty(notes = "Date when customer returns the car")
    @NotNull
    private LocalDate dropOffDate;

    @ApiModelProperty(notes = "Booking status. By default is Pending.")
    private String status;
}
