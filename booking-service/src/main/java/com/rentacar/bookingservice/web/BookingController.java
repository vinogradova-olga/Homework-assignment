package com.rentacar.bookingservice.web;

import com.rentacar.bookingservice.business.service.BookingService;
import com.rentacar.bookingservice.model.Booking;
import com.rentacar.bookingservice.swagger.DescriptionVariables;
import com.rentacar.bookingservice.swagger.HTMLResponseMessages;
import io.swagger.annotations.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Api(tags = {DescriptionVariables.BOOKING_SERVICE})
@Log4j2
@RestController
@Validated
@RequestMapping("/api/v1/booking")
public class BookingController {

    private BookingService bookingService;

    @Autowired
    public void setBookingService(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Find the booking by id",
            notes = "Provide an id to search specific booking in database",
            response = Booking.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HTMLResponseMessages.HTTP_200),
            @ApiResponse(code = 400, message = HTMLResponseMessages.HTTP_400),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500)})
    public ResponseEntity<Optional<Booking>> findBookingById(@ApiParam(value = "id of the booking", required = true)
                                                       @NonNull @PathVariable Long id) {
        log.info("Find booking by passing ID of the booking, where booking ID is :{} ", id);
        Optional<Booking> booking = bookingService.findBookingById(id);
        if (!booking.isPresent()) {
            log.warn("Booking with id {} is not found.", id);
            return ResponseEntity.notFound().build();
        } else {
            log.debug("Booking with id {} is found: {}", id, booking);
            return ResponseEntity.ok(booking);
        }
    }
    @GetMapping
    @ApiOperation(value = "Finds all bookings",
            notes = "Returns the list of registered bookings",
            response = Booking.class,
            responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HTMLResponseMessages.HTTP_200),
            @ApiResponse(code = 400, message = HTMLResponseMessages.HTTP_400),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500)})
    public ResponseEntity<List<Booking>> findAllBookings() {
        log.info("Retrieve list of bookings");
        List<Booking> bookingList = bookingService.findAllBookings();
        if (bookingList.isEmpty()) {
            log.warn("Booking list is empty.");
            return ResponseEntity.noContent().build();
        }
        log.debug("Booking list size: {}", bookingList.size());
        return ResponseEntity.ok(bookingList);
    }
    @GetMapping("/user/{userId}")
    @ApiOperation(value = "Find all bookings by user ID",
            notes = "Provide a user ID to retrieve all bookings associated with that user",
            response = Booking.class,
            responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HTMLResponseMessages.HTTP_200),
            @ApiResponse(code = 400, message = HTMLResponseMessages.HTTP_400),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500)})
    public ResponseEntity<List<Booking>> findBookingsByUserId(@ApiParam(value = "User ID", required = true)
                                                              @NonNull @PathVariable Long userId) {
        log.info("Find bookings by user ID: {}", userId);
        List<Booking> bookings = bookingService.findBookingsByUserId(userId);
        if (bookings.isEmpty()) {
            log.warn("No bookings found for user with ID: {}", userId);
            return ResponseEntity.noContent().build();
        }
        log.debug("Found {} bookings for user with ID: {}", bookings.size(), userId);
        return ResponseEntity.ok(bookings);
    }

    @PostMapping()
    @ApiOperation(value = "Saves the booking to the database",
            response = Booking.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = HTMLResponseMessages.HTTP_200),
            @ApiResponse(code = 400, message = HTMLResponseMessages.HTTP_400),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500)})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Booking> saveBooking (@Valid @RequestBody Booking booking){
        log.info("Received values {}", booking);
        if (booking.getPickUpDate().isAfter(booking.getDropOffDate())) {
            log.error("Invalid booking dates. Pick-up date must be before drop-off date.");
            throw new IllegalArgumentException("Pick-up date must be before drop-off date.");
        }
        if (bookingService.hasOverlappingBookings(booking)) {
            log.error("Overlapping bookings detected.");
            throw new IllegalArgumentException("Overlapping bookings detected.");
        }
        Booking bookingToSave = bookingService.saveBooking(booking);
        log.info("New booking is created: {}", bookingToSave);
        return new ResponseEntity<>(bookingToSave, HttpStatus.CREATED);
    }


    @PutMapping("/{id}/cancel")
    @ApiOperation(value = "Cancel a booking by ID",
                  notes = "Updates the booking status to 'Cancelled'",
                  response = Booking.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = HTMLResponseMessages.HTTP_201),
            @ApiResponse(code = 400, message = HTMLResponseMessages.HTTP_400),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500)})
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<Booking> cancelBooking(@ApiParam(value="id of the booking", required = true)
                                                     @NotNull @PathVariable Long id,
                                                 @Valid @RequestBody Booking booking) {
        Booking canceledBooking = bookingService.cancelBooking(booking);
        return new ResponseEntity<>(canceledBooking, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/confirm")
    @ApiOperation(value = "Confirm a booking by ID",
            notes = "Updates the booking status to 'Confirmed'",
            response = Booking.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = HTMLResponseMessages.HTTP_201),
            @ApiResponse(code = 400, message = HTMLResponseMessages.HTTP_400),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500)})
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<Booking> confirmBooking(@ApiParam(value="id of the booking", required = true)
                                                 @NotNull @PathVariable Long id,
                                                 @Valid @RequestBody Booking booking) {
        Booking confirmedBooking = bookingService.confirmBooking(booking);
        return new ResponseEntity<>(confirmedBooking, HttpStatus.CREATED);
    }
}
