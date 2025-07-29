package com.rentacar.carinventory.business.service;

import com.rentacar.carinventory.model.Car;

import java.util.List;
import java.util.Optional;

public interface CarInventoryService {
    Car saveCar(Car car);

    Car updateCar(Car car);

    List<Car> findAllCars();

    Optional<Car> findCarById(Long id);

    void deleteCarById(Long id);

    List<Car> findAvailableCars();

    List<Car> findAllAvailableCarsSortedByPriceAsc();
}
