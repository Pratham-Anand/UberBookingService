package com.example.UberBookingService.repositories;


import com.example.UberEntityService.models.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepository extends JpaRepository<Driver,Long> {
}
