package com.rentacar.carinventory.web;

import com.rentacar.carinventory.business.service.CarInventoryService;
import com.rentacar.carinventory.model.Car;
import com.rentacar.carinventory.swagger.DescriptionVariables;
import com.rentacar.carinventory.swagger.HTMLResponseMessages;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;


import java.util.List;
import java.util.Optional;

@Api(tags = {DescriptionVariables.CAR_INVENTORY})
@Log4j2
@RestController
@Validated
@RequestMapping("/api/v1/car")
public class CarInventoryController {
    private CarInventoryService carInventoryService;

    @Autowired
    public void setCarInventoryService(CarInventoryService carInventoryService) {
        this.carInventoryService = carInventoryService;
    }

    @GetMapping
    @ApiOperation(value = "Finds all cars",
            notes = "Returns the list of cars",
            response = Car.class,
            responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HTMLResponseMessages.HTTP_200),
            @ApiResponse(code = 400, message = HTMLResponseMessages.HTTP_400),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500)})
    public ResponseEntity<List<Car>> findAllCars() {
        log.info("Retrieve list of cars");
        List<Car> carList = carInventoryService.findAllCars();
        if (carList.isEmpty()) {
            log.warn("Car list is empty.");
            return ResponseEntity.noContent().build();
        }
        log.debug("Car list size: {}", carList.size());
        return ResponseEntity.ok(carList);
    }
    @GetMapping("/available")
    @ApiOperation(value = "Finds all available cars",
            notes = "Returns the list of cars that are available for renting",
            response = Car.class,
            responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HTMLResponseMessages.HTTP_200),
            @ApiResponse(code = 400, message = HTMLResponseMessages.HTTP_400),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500)})
    public ResponseEntity<List<Car>> findAvailableCars() {
        log.info("Retrieve list of available cars");
        List<Car> availableCars = carInventoryService.findAvailableCars();
        if (availableCars.isEmpty()) {
            log.warn("No available cars found.");
            return ResponseEntity.noContent().build();
        }
        log.debug("Available car list size: {}", availableCars.size());
        return ResponseEntity.ok(availableCars);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Find the car by id",
            notes = "Provide an id to search specific car in database",
            response = Car.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HTMLResponseMessages.HTTP_200),
            @ApiResponse(code = 400, message = HTMLResponseMessages.HTTP_400),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500)})
    public ResponseEntity<Optional<Car>> findCarById(@ApiParam(value = "id of the car", required = true)
                                                     @NonNull @PathVariable Long id) {
        log.info("Find car by passing ID of the car, where car ID is :{} ", id);
        Optional<Car> car = carInventoryService.findCarById(id);
        if (!car.isPresent()) {
            log.warn("Car with id {} is not found.", id);
            return ResponseEntity.notFound().build();
        } else {
            log.debug("Car with id {} is found: {}", id, car);
            return ResponseEntity.ok(car);
        }
    }

    @GetMapping("/available/sortedByPrice") //ascend, for descending ",desc"
    @ApiOperation(value = "Finds all available cars sorted by price",
            notes = "Returns the list of cars that are available for renting, sorted by price in ascending order",
            response = Car.class,
            responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HTMLResponseMessages.HTTP_200),
            @ApiResponse(code = 400, message = HTMLResponseMessages.HTTP_400),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500)})
    public ResponseEntity<List<Car>> findAvailableCarsSortedByPrice () {
        log.info("Retrieve list of available cars sorted by price");
        List<Car> sortedCars = carInventoryService.findAllAvailableCarsSortedByPriceAsc();
        if (sortedCars.isEmpty()) {
            log.warn("No available cars found.");
            return ResponseEntity.noContent().build();
        }
        log.debug("Sorted available car list size: {}", sortedCars.size());
        return ResponseEntity.ok(sortedCars);
    }

    @PostMapping()
    @ApiOperation(value = "Saves the car to the database",
    response = Car.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = HTMLResponseMessages.HTTP_200),
            @ApiResponse(code = 400, message = HTMLResponseMessages.HTTP_400),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500)})
    @ResponseStatus(HttpStatus.CREATED)

    public ResponseEntity<Car> saveCar (@Valid @RequestBody Car car){
        log.info("Received values {}", car);
        Car carToSave = carInventoryService.saveCar(car);
        log.info("New car is created: {}", carToSave);
        return new ResponseEntity<>(carToSave, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Updates the car by id",
    notes = "Updates the car if provided id exists",
    response = Car.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = HTMLResponseMessages.HTTP_201),
            @ApiResponse(code = 400, message = HTMLResponseMessages.HTTP_400),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500)})
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<Car> updateCarById(@ApiParam(value="id of the car", required = true)
                                             @NotNull @PathVariable Long id,
                                             @Valid @RequestBody Car car){
        if (id <= 0) {
            log.warn("Provided car id {} is wrong", id);
            return ResponseEntity.badRequest().build();
        }
        if (!id.equals(car.getId())) {
            log.warn("Car for update with id {} is not found", id);
            return ResponseEntity.notFound().build();
        }
        log.info("Update existing car with Id: {} and new body: {}", id, car);
        carInventoryService.updateCar(car);
        log.debug("Car with id {} is updated: {}", id, car);
        return new ResponseEntity<>(car, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Deletes the car by id",
            notes = "Deletes the car if provided id exists",
            response = Car.class)
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = HTMLResponseMessages.HTTP_204_WITHOUT_DATA),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500)})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteCarById(@ApiParam(value = "The id of the car", required = true)
                                                   @NotNull @PathVariable Long id) {
        if (id <= 0) {
            log.warn("Invalid car id: {}", id);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Optional<Car> car = carInventoryService.findCarById(id);
        if (!car.isPresent()) {
            log.warn("Car with id {} is not found", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        carInventoryService.deleteCarById(id);
        log.info("Car with id {} is deleted", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
