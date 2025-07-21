package com.example.UberBookingService.repositories;


import com.example.UberEntityService.models.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking,Long> {
}
