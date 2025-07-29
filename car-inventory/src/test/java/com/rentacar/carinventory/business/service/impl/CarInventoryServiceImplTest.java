package com.rentacar.carinventory.business.service.impl;

import com.rentacar.carinventory.business.mappers.CarInventoryMapStructMapper;
import com.rentacar.carinventory.business.repository.CarInventoryRepository;
import com.rentacar.carinventory.business.repository.model.CarDAO;
import com.rentacar.carinventory.model.Car;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

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
class CarInventoryServiceImplTest {
    @Mock
    private CarInventoryRepository carInventoryRepository;

    @InjectMocks
    private CarInventoryServiceImpl carInventoryServiceImpl;

    @Mock
    CarInventoryMapStructMapper mapper;

    private CarDAO carDAO;

    private Car car;

    @BeforeEach
    public void init(){
        car = createCar();
        carDAO = createCarDAO();
    }

    private Car createCar() {
        return Car.builder()
                .id(1L)
                .make("Volvo")
                .model("v60")
                .gearbox("automatic")
                .fuel("gasoline")
                .year(2018)
                .price(70.00)
                .available(true).build();
    }

    private CarDAO createCarDAO() {
        return CarDAO.builder()
                .id(1L)
                .make("Volvo")
                .model("v60")
                .gearbox("automatic")
                .fuel("gasoline")
                .year(2018)
                .price(70.00)
                .available(true).build();
    }

    private CarDAO createCarDAO2() {
        return CarDAO.builder()
                .id(2L)
                .make("Volvo")
                .model("s60")
                .gearbox("automatic")
                .fuel("diesel")
                .year(2016)
                .price(45.00)
                .available(true).build();
    }


    private List<CarDAO> createCarDAOList(){
        List<CarDAO> carDAOS = new ArrayList<>();
        carDAOS.add(createCarDAO());
        carDAOS.add(createCarDAO2());
        return carDAOS;
    }

    @Test
    void saveCar() {
        when(carInventoryRepository.save(carDAO)).thenReturn(carDAO);
        when(mapper.carDAOToCar(carDAO)).thenReturn(car);
        when(mapper.carToCarDAO(car)).thenReturn(carDAO);

        Car carSaved = carInventoryServiceImpl.saveCar(car);

        assertEquals(car, carSaved);
        verify(carInventoryRepository, times(1)).save(carDAO);
    }

    @Test
    void updateCar() {
        when(carInventoryRepository.save(any())).thenReturn(carDAO);
        when(mapper.carDAOToCar(carDAO)).thenReturn(car);
        when(mapper.carToCarDAO(car)).thenReturn(carDAO);
        when(carInventoryRepository.findById(anyLong())).thenReturn(Optional.of(carDAO));
        Optional<Car> carOptional = carInventoryServiceImpl.findCarById(car.getId());
        Car updatedCar = carInventoryServiceImpl.updateCar(car);
        assertEquals(car.getId(), updatedCar.getId());
        verify(carInventoryRepository, times(1)).save(carDAO);
    }

    @Test
    void findAllCars() {
        when(carInventoryRepository.findAll()).thenReturn(createCarDAOList());
        List<Car> car = carInventoryServiceImpl.findAllCars();
        assertEquals(2,car.size());
        verify(carInventoryRepository, times(1)).findAll();
    }

    @Test
    void findCarById() {
        when(carInventoryRepository.findById(anyLong())).thenReturn(Optional.of(carDAO));
        when(mapper.carDAOToCar(carDAO)).thenReturn(car);
        Optional<Car> carOptional = carInventoryServiceImpl.findCarById(car.getId());
        assertEquals(car.getId(),carOptional.get().getId());
        assertEquals(car.getMake(),carOptional.get().getMake());
        verify(carInventoryRepository, times(1)).findById(car.getId());
    }

    @Test
    void deleteCarById() {
        Long id = createCar().getId();
        carInventoryServiceImpl.deleteCarById(id);
        verify(carInventoryRepository, times(1)).deleteById(id);
    }

    @Test
    void findAvailableCars() {
        when(carInventoryRepository.findAll()).thenReturn(createCarDAOList());
        List<Car> availableCars = carInventoryServiceImpl.findAvailableCars();
        assertEquals(2,availableCars.size());
    }

    @Test
    void findAllAvailableCarsSortedByPriceAsc() {
        when(carInventoryRepository.findAll(Sort.by(Sort.Direction.ASC, "price"))).thenReturn(createCarDAOList());
        List<Car> availableCarsSortedByPrice = carInventoryServiceImpl.findAllAvailableCarsSortedByPriceAsc();
        assertEquals(2,availableCarsSortedByPrice.size());
    }
}