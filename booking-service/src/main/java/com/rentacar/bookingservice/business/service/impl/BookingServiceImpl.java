package com.rentacar.bookingservice.business.service.impl;

import com.rentacar.bookingservice.business.mappers.BookingMapStructMapper;
import com.rentacar.bookingservice.business.repository.BookingRepository;
import com.rentacar.bookingservice.business.repository.model.BookingDAO;
import com.rentacar.bookingservice.business.service.BookingService;
import com.rentacar.bookingservice.model.Booking;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
public class BookingServiceImpl implements BookingService {

    private BookingRepository bookingRepository;

    private BookingMapStructMapper bookingMapStructMapper;

    @Autowired
    public void setBookingRepository(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Autowired
    public void setBookingMapStructMapper(BookingMapStructMapper bookingMapStructMapper) {
        this.bookingMapStructMapper = bookingMapStructMapper;
    }

    @Override
    public Optional<Booking> findBookingById(Long id) {
        Optional<Booking> bookingById = bookingRepository.findById(id)
                .flatMap(booking -> Optional.ofNullable(bookingMapStructMapper.bookingDAOToBooking(booking)));
        log.info("Booking with id {} is {}", id, bookingById);
        return bookingById;
    }

    @Override
    public List<Booking> findAllBookings() {
        List<BookingDAO> bookingDAOList = bookingRepository.findAll();
        log.info("Get booking list. Size is: {}", bookingDAOList::size);
        return bookingDAOList.stream().map(bookingMapStructMapper::bookingDAOToBooking).collect(Collectors.toList());
    }

    @Override
    public List<Booking> findBookingsByUserId(Long userId) {
        List<BookingDAO> bookingDAOList = bookingRepository.findByUserId(userId);
        long listSize = bookingDAOList.size();
        log.info("Get booking list for user with ID {}. Size is: {}", userId, listSize);
        return bookingDAOList.stream()
                .map(bookingMapStructMapper::bookingDAOToBooking)
                .collect(Collectors.toList());
    }

    @Override
    public Booking saveBooking(Booking booking) {
        booking.setStatus("Pending");
        BookingDAO bookingDAO = bookingMapStructMapper.bookingToBookingDAO(booking);
        BookingDAO savedBooking = bookingRepository.save(bookingDAO);
        log.info("New booking is saved: {}", savedBooking);
        return bookingMapStructMapper.bookingDAOToBooking(bookingDAO);
    }

    @Override
    public boolean hasOverlappingBookings(Booking booking) {
        List<BookingDAO> overlappingBookings = bookingRepository.findOverlappingBookings(
                booking.getCarId(),
                booking.getPickUpDate(),
                booking.getDropOffDate()
        );
        return !overlappingBookings.isEmpty();
    }

    @Override
    public Booking cancelBooking(Booking booking) {
        Long id = booking.getId();

        Optional<BookingDAO> optionalBookingDAO = bookingRepository.findById(id);
        if (optionalBookingDAO.isEmpty()) {
            log.error("Booking with id {} not found", id);
            throw new EntityNotFoundException("Booking not found for id: " + id);
        }

        BookingDAO existingBookingDAO = optionalBookingDAO.get();
        existingBookingDAO.setStatus("Canceled");
        BookingDAO canceledBookingDAO = bookingRepository.save(existingBookingDAO);
        log.info("Booking with id {} canceled: {}", id, canceledBookingDAO);
        return bookingMapStructMapper.bookingDAOToBooking(canceledBookingDAO);
    }

    @Override
    public Booking confirmBooking(Booking booking) {
        Long id = booking.getId();

        Optional<BookingDAO> optionalBookingDAO = bookingRepository.findById(id);
        if (optionalBookingDAO.isEmpty()) {
            log.error("Booking with id {} not found", id);
            throw new EntityNotFoundException("Booking not found for id: " + id);
        }

        BookingDAO existingBookingDAO = optionalBookingDAO.get();
        existingBookingDAO.setStatus("Confirmed");
        BookingDAO confirmedBookingDAO = bookingRepository.save(existingBookingDAO);
        log.info("Booking with id {} confirmed: {}", id, confirmedBookingDAO);
        return bookingMapStructMapper.bookingDAOToBooking(confirmedBookingDAO);
    }
}
