package com.rentacar.bookingservice.business.mappers;

import com.rentacar.bookingservice.business.repository.model.BookingDAO;
import com.rentacar.bookingservice.model.Booking;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookingMapStructMapper {
    BookingDAO bookingToBookingDAO (Booking booking);

    Booking bookingDAOToBooking (BookingDAO bookingDAO);
}
