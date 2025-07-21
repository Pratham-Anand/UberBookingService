package com.example.UberBookingService.repositories;


import com.example.UberEntityService.models.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PassengerRepository extends JpaRepository<Passenger,Long> {

}
