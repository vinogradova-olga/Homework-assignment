package com.rentacar.carinventory.business.service.impl;

import com.rentacar.carinventory.business.mappers.CarInventoryMapStructMapper;
import com.rentacar.carinventory.business.repository.CarInventoryRepository;
import com.rentacar.carinventory.business.repository.model.CarDAO;
import com.rentacar.carinventory.business.service.CarInventoryService;
import com.rentacar.carinventory.model.Car;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
public class CarInventoryServiceImpl implements CarInventoryService {
    private CarInventoryRepository carInventoryRepository;

    private CarInventoryMapStructMapper carInventoryMapStructMapper;

    @Autowired
    public void setCarInventoryRepository(CarInventoryRepository carInventoryRepository){
        this.carInventoryRepository = carInventoryRepository;
    }

    @Autowired
    public void setCarInventoryMapStructMapper(CarInventoryMapStructMapper carInventoryMapStructMapper){
        this.carInventoryMapStructMapper = carInventoryMapStructMapper;
    }

    @Override
    public Car saveCar(Car car) {
        CarDAO carDAO = carInventoryMapStructMapper.carToCarDAO(car);
        CarDAO savedCar = carInventoryRepository.save(carDAO);
        log.info("New car is saved: {}", savedCar);
        return carInventoryMapStructMapper.carDAOToCar(carDAO);
    }

    @Override
    public Car updateCar(Car car) {
        Long id = car.getId();
        if (!carInventoryRepository.findById(id).isPresent()) {
            log.error("Car is not found by id: {}", HttpStatus.BAD_REQUEST);
            throw new EntityNotFoundException("Car is not found by id");
        }
        CarDAO updatedCar =
                carInventoryRepository.save(carInventoryMapStructMapper.carToCarDAO(car));
        log.info("Car updated: {}", () -> updatedCar);
        return carInventoryMapStructMapper.carDAOToCar(updatedCar);
    }

    @Override
    public List<Car> findAllCars() {
        List<CarDAO> carDAOList = carInventoryRepository.findAll();
        log.info("Get car list. Size is: {}", carDAOList::size);
        return carDAOList.stream().map(carInventoryMapStructMapper::carDAOToCar).collect(Collectors.toList());
    }

    @Override
    public Optional<Car> findCarById(Long id) {
        Optional<Car> carById = carInventoryRepository.findById(id)
                .flatMap(car -> Optional.ofNullable(carInventoryMapStructMapper.carDAOToCar(car)));
        log.info("Car with id {} is {}", id, carById);
        return carById;
    }

    @Override
    public void deleteCarById(Long id) {
        carInventoryRepository.deleteById(id);
        log.info("Car with id {} is deleted", id);
    }

    @Override
    public List<Car> findAvailableCars() {
        return carInventoryRepository.findAll().stream()
                .filter(CarDAO::isAvailable)
                .map(carInventoryMapStructMapper::carDAOToCar)
                .collect(Collectors.toList());
    }

    @Override
    public List<Car> findAllAvailableCarsSortedByPriceAsc() {
        List<CarDAO> sortedCarDAOs = carInventoryRepository.findAll(Sort.by(Sort.Direction.ASC, "price"));
        return sortedCarDAOs.stream()
                .filter(car -> car.isAvailable())
                .map(carInventoryMapStructMapper::carDAOToCar)
                .collect(Collectors.toList());
    }
}
