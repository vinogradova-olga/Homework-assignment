package com.rentacar.bookingservice.business.service.impl;

import com.rentacar.bookingservice.business.mappers.BookingMapStructMapper;
import com.rentacar.bookingservice.business.repository.BookingRepository;
import com.rentacar.bookingservice.business.repository.model.BookingDAO;
import com.rentacar.bookingservice.model.Booking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingServiceImpl bookingServiceImpl;

    @Mock
    BookingMapStructMapper mapper;

    private BookingDAO bookingDAO;

    private Booking booking;

    @BeforeEach
    public void init(){
        booking = createBooking();
        bookingDAO = createBookingDAO();
    }

    private Booking createBooking() {
        return Booking.builder()
                .id(1L)
                .userId(1L)
                .pickUpDate(LocalDate.parse("2024-04-01"))
                .dropOffDate(LocalDate.parse("2024-04-05")).build();
    }
    private BookingDAO createBookingDAO() {
        return BookingDAO.builder()
                .id(1L)
                .userId(1L)
                .pickUpDate(LocalDate.parse("2024-04-01"))
                .dropOffDate(LocalDate.parse("2024-04-05")).build();
    }

    private BookingDAO createBookingDAO2() {
        return BookingDAO.builder()
                .id(2L)
                .userId(1L)
                .pickUpDate(LocalDate.parse("2024-05-01"))
                .dropOffDate(LocalDate.parse("2024-05-05")).build();
    }
    private List<BookingDAO> createBookingDAOList(){
        List<BookingDAO> bookingDAOS = new ArrayList<>();
        bookingDAOS.add(createBookingDAO());
        bookingDAOS.add(createBookingDAO2());
        return bookingDAOS;
    }

    @Test
    void findBookingById() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(bookingDAO));
        when(mapper.bookingDAOToBooking(bookingDAO)).thenReturn(booking);
        Optional<Booking> bookingOptional = bookingServiceImpl.findBookingById(booking.getId());
        assertEquals(booking.getId(),bookingOptional.get().getId());
        assertEquals(booking.getPickUpDate(),bookingOptional.get().getPickUpDate());
        verify(bookingRepository, times(1)).findById(booking.getId());
    }

    @Test
    void findAllBookings() {
        when(bookingRepository.findAll()).thenReturn(createBookingDAOList());
        List<Booking> booking = bookingServiceImpl.findAllBookings();
        assertEquals(2,booking.size());
        verify(bookingRepository, times(1)).findAll();
    }

    @Test
    void findBookingsByUserId() {
        when(bookingRepository.findByUserId(1L)).thenReturn(createBookingDAOList());
        List<Booking> booking = bookingServiceImpl.findBookingsByUserId(1L);
        assertEquals(2, booking.size());
        verify(bookingRepository, times(1)).findByUserId(1L);
    }

    @Test
    void saveBooking() {
        when(bookingRepository.save(bookingDAO)).thenReturn(bookingDAO);
        when(mapper.bookingDAOToBooking(bookingDAO)).thenReturn(booking);
        when(mapper.bookingToBookingDAO(booking)).thenReturn(bookingDAO);

        Booking bookingSaved = bookingServiceImpl.saveBooking(booking);

        assertEquals(booking, bookingSaved);
        verify(bookingRepository, times(1)).save(bookingDAO);
    }

    @Test
    void cancelBooking() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(bookingDAO));
        when(bookingRepository.save(any())).thenReturn(bookingDAO);
        when(mapper.bookingDAOToBooking(bookingDAO)).thenReturn(booking);

        Booking canceledBooking = bookingServiceImpl.cancelBooking(booking);

        assertEquals(booking.getStatus(), canceledBooking.getStatus());
        verify(bookingRepository, times(1)).findById(booking.getId());
        verify(bookingRepository, times(1)).save(bookingDAO);
    }

    @Test
    void confirmBooking() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(bookingDAO));
        when(bookingRepository.save(any())).thenReturn(bookingDAO);
        when(mapper.bookingDAOToBooking(bookingDAO)).thenReturn(booking);

        Booking confirmedBooking = bookingServiceImpl.confirmBooking(booking);

        assertEquals(booking.getStatus(), confirmedBooking.getStatus());
        verify(bookingRepository, times(1)).findById(booking.getId());
        verify(bookingRepository, times(1)).save(bookingDAO);
    }
}