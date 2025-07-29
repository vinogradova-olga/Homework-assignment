package com.rentacar.bookingservice.business.service;

import com.rentacar.bookingservice.model.Booking;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingService {
    Optional<Booking> findBookingById(Long id);

    List<Booking> findAllBookings();

    List<Booking> findBookingsByUserId(Long userId);

    Booking cancelBooking(Booking booking);

    Booking confirmBooking(Booking booking);

    Booking saveBooking(Booking booking);

    boolean hasOverlappingBookings(Booking booking);
}
