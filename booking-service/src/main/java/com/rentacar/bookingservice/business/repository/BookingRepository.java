package com.rentacar.bookingservice.business.repository;

import com.rentacar.bookingservice.business.repository.model.BookingDAO;
import com.rentacar.bookingservice.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<BookingDAO, Long> {
    List<BookingDAO> findByUserId(Long userId);

    @Query("SELECT b FROM BookingDAO b WHERE b.carId = :carId " +
            "AND ((b.pickUpDate <= :endDate AND b.dropOffDate >= :startDate) " +
            "OR (b.pickUpDate BETWEEN :startDate AND :endDate) " +
            "OR (b.dropOffDate BETWEEN :startDate AND :endDate))")
    List<BookingDAO> findOverlappingBookings(@Param("carId") Long carId,
                                             @Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);
}
