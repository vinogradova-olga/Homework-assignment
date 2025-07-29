package com.rentacar.bookingservice.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rentacar.bookingservice.business.service.BookingService;
import com.rentacar.bookingservice.model.Booking;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    public static String baseUrl = "/api/v1/booking";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    private Booking booking;

    @BeforeEach
    public void init() {
        booking = bookingToTest();
    }

    private Booking bookingToTest() {
        return Booking.builder()
                .id(1L)
                .pickUpDate(LocalDate.parse("2024-04-01"))
                .dropOffDate(LocalDate.parse("2024-04-05")).build();
    }

    @Test
    void findBookingById_success() throws Exception{
        Mockito.when(bookingService.findBookingById(booking.getId())).thenReturn(Optional.of(booking));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(baseUrl+ "/{id}", booking.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.pickUpDate", is("2024-04-01")));
    }

    @Test
    void findBookingById_notFound() throws Exception{
        Long id = 5L;
        when(bookingService.findBookingById(id)).thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders
                        .get(baseUrl + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAllBookings_success() throws Exception{
        Booking secondBooking = Booking.builder()
                .id(2L)
                .pickUpDate(LocalDate.parse("2024-05-01"))
                .dropOffDate(LocalDate.parse("2024-05-05")).build();


        List<Booking> bookingList = new ArrayList<>(Arrays.asList(booking,secondBooking));
        Mockito.when(bookingService.findAllBookings()).thenReturn(bookingList);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(baseUrl)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].pickUpDate",is("2024-04-01")))
                .andExpect(jsonPath("$[1].pickUpDate",is("2024-05-01")));
    }

    @Test
    void findAllBookings_noContent() throws Exception {
        List<Booking> emptyList = new ArrayList<>();
        Mockito.when(bookingService.findAllBookings()).thenReturn(emptyList);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(baseUrl)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void findBookingsByUserId_success() throws Exception{
        Long userId = 1L;

        Booking userBooking1 = Booking.builder()
                .id(1L)
                .userId(userId)
                .pickUpDate(LocalDate.parse("2024-05-01"))
                .dropOffDate(LocalDate.parse("2024-05-05"))
                .build();

        Booking userBooking2 = Booking.builder()
                .id(2L)
                .userId(userId)
                .pickUpDate(LocalDate.parse("2024-06-01"))
                .dropOffDate(LocalDate.parse("2024-06-05"))
                .build();

        List<Booking> userBookings = Arrays.asList(userBooking1, userBooking2);

        Mockito.when(bookingService.findBookingsByUserId(userId)).thenReturn(userBookings);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(baseUrl + "/user/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].pickUpDate", is("2024-05-01")))
                .andExpect(jsonPath("$[1].pickUpDate", is("2024-06-01")));
    }

    @Test
    void findBookingsByUserId_noContent() throws Exception {
        Long userId = 1L;

        Mockito.when(bookingService.findBookingsByUserId(userId)).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders
                        .get(baseUrl + "/user/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void saveBooking_success() throws Exception{
        Mockito.when(bookingService.saveBooking(booking)).thenReturn(booking);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(booking));

        mockMvc.perform(mockRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pickUpDate",is("2024-04-01")));
    }

    @Test
    void cancelBooking() throws Exception{
        booking.setStatus("Canceled");
        when(bookingService.findBookingById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingService.cancelBooking(booking)).thenReturn(booking);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .put(baseUrl + "/{id}/cancel", booking.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(booking));

        mockMvc.perform(mockRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("Canceled")));

        verify(bookingService, times(1)).cancelBooking(booking);
    }

    @Test
    void confirmBooking() throws Exception{
        booking.setStatus("Confirmed");
        when(bookingService.findBookingById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingService.confirmBooking(booking)).thenReturn(booking);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .put(baseUrl + "/{id}/confirm", booking.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(booking));

        mockMvc.perform(mockRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("Confirmed")));

        verify(bookingService, times(1)).confirmBooking(booking);

    }
}