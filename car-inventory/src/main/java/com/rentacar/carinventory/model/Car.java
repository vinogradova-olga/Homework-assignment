package com.rentacar.carinventory.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(description = "Model of car data")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Car {

    @ApiModelProperty(notes = "The unique id of the car")
    private Long id;

    @ApiModelProperty(notes = "The manufacturer brand of the vehicle")
    @NotNull
    private String make;

    @ApiModelProperty(notes = "Model of a car")
    @NotNull
    private String model;

    @ApiModelProperty(notes = "Type of transmission")
    @NotNull
    private String gearbox;

    @ApiModelProperty(notes = "Fuel type")
    @NotNull
    private String fuel;

    @ApiModelProperty(notes = "The manufacture year of a car")
    @NotNull
    private int year;

    @ApiModelProperty(notes = "Rental price in euro per day")
    @NotNull
    private double price;

    @ApiModelProperty(notes = "Availability")
    private boolean available;
}
