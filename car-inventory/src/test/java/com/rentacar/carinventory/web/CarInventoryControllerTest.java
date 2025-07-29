package com.rentacar.carinventory.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rentacar.carinventory.business.service.CarInventoryService;
import com.rentacar.carinventory.model.Car;
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



@WebMvcTest(CarInventoryController.class)
class CarInventoryControllerTest {
    public static String baseUrl = "/api/v1/car";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CarInventoryService carInventoryService;

    private Car car;

    @BeforeEach
    public void init() {
        car = carToTest();
    }

    private Car carToTest() {
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

    @Test
    void findAllCars_success() throws Exception{
        Car secondCar = Car.builder()
                .id(2L)
                .make("Volkswagen")
                .model("Tiguan")
                .gearbox("manual")
                .fuel("diesel")
                .year(2019)
                .price(75.00)
                .available(false).build();

        List<Car> carList = new ArrayList<>(Arrays.asList(car,secondCar));
        Mockito.when(carInventoryService.findAllCars()).thenReturn(carList);

        mockMvc.perform(MockMvcRequestBuilders
                .get(baseUrl)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].make",is("Volvo")))
                .andExpect(jsonPath("$[1].make",is("Volkswagen")));
    }
    @Test
    void findAllCars_noContent() throws Exception {
        List<Car> emptyList = new ArrayList<>();
        Mockito.when(carInventoryService.findAllCars()).thenReturn(emptyList);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(baseUrl)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void findAvailableCars_success() throws Exception{
        Car availableCar = Car.builder()
                .id(2L)
                .make("Skoda")
                .model("Karoq")
                .gearbox("automatic")
                .fuel("diesel")
                .year(2020)
                .price(85.00)
                .available(true)
                .build();

        List<Car> availableCarsList = new ArrayList<>(Arrays.asList(car,availableCar));
        Mockito.when(carInventoryService.findAvailableCars()).thenReturn(availableCarsList);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(baseUrl + "/available")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[1].make", is("Skoda")));
    }

    @Test
    void findAvailableCars_noContent() throws Exception{
        List<Car> emptyList = new ArrayList<>();
        Mockito.when(carInventoryService.findAvailableCars()).thenReturn(emptyList);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(baseUrl + "/available")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void findCarById_success() throws Exception {
        Mockito.when(carInventoryService.findCarById(car.getId())).thenReturn(Optional.of(car));

        mockMvc.perform(MockMvcRequestBuilders
                .get(baseUrl+ "/{id}", car.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.make", is("Volvo")));
    }

    @Test
    void findCarById_notFound() throws Exception{
        Long id = 5L;
        when(carInventoryService.findCarById(id)).thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders
                        .get(baseUrl + "/5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAvailableCarsSortedByPrice() throws Exception{
        Car car2 = Car.builder()
                .id(2L)
                .make("Skoda")
                .model("Karoq")
                .gearbox("automatic")
                .fuel("diesel")
                .year(2020)
                .price(85.00)
                .available(true)
                .build();

        List<Car> sortedCarsList = new ArrayList<>(Arrays.asList(car,car2));
        Mockito.when(carInventoryService.findAllAvailableCarsSortedByPriceAsc()).thenReturn(sortedCarsList);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(baseUrl + "/available/sortedByPrice")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].make", is("Volvo")))
                .andExpect(jsonPath("$[1].make", is("Skoda")));
    }

    @Test
    void findAvailableCarsSortedByPrice_noContent() throws Exception {
        List<Car> emptyList = new ArrayList<>();
        Mockito.when(carInventoryService.findAllAvailableCarsSortedByPriceAsc()).thenReturn(emptyList);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(baseUrl + "/available/sortedByPrice")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void saveCar_success() throws Exception {
        Mockito.when(carInventoryService.saveCar(car)).thenReturn(car);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(car));

        mockMvc.perform(mockRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.make",is("Volvo")));
    }

    @Test
    void updateCarById_success() throws Exception {
        Car updatedCar = Car.builder()
                .id(1L)
                .make("Volkswagen")
                .model("Tiguan")
                .gearbox("manual")
                .fuel("diesel")
                .year(2019)
                .price(75.00)
                .available(false).build();

        when(carInventoryService.findCarById(car.getId())).thenReturn(Optional.of(car));
        when(carInventoryService.updateCar(updatedCar)).thenReturn(updatedCar);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .put(baseUrl + "/{id}", car.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updatedCar));

        mockMvc.perform(mockRequest)
                .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.make", is("Volkswagen")));

        verify(carInventoryService, times(1)).updateCar(updatedCar);
    }

    @Test
    public void updateCarById_invalidId() throws Exception {
        Long invalidId = 0L;
        mockMvc.perform(put(baseUrl + "/{id}", invalidId))
                .andExpect(status().isBadRequest());
        verify(carInventoryService, times(0)).updateCar(null);
    }

    @Test
    public void updateCarById_notFound() throws Exception {
        Long id = 2L;
        when(carInventoryService.findCarById(id)).thenReturn(Optional.empty());
        this.mockMvc.perform(MockMvcRequestBuilders
                        .put(baseUrl + "/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(car))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(carInventoryService, times(0)).updateCar(car);
    }

    @Test
    void deleteCarById_success() throws Exception{
        Long id = car.getId();
        when(carInventoryService.findCarById(id)).thenReturn(Optional.of(new Car()));
        mockMvc.perform(delete(baseUrl + "/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteCarById_notExistingId() throws Exception{
        Long id = car.getId();
        when(carInventoryService.findCarById(id)).thenReturn(Optional.empty());
        mockMvc.perform(delete(baseUrl + "/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteCarById_invalidId() throws Exception{
        Long id = 0L;
        mockMvc.perform(delete(baseUrl + "/{id}", id))
                .andExpect(status().isBadRequest());
    }
}