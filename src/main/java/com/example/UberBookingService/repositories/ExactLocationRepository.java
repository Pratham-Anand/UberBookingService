package com.example.UberBookingService.repositories;

import com.example.UberEntityService.models.ExactLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExactLocationRepository extends JpaRepository<ExactLocation,Long> {
}
