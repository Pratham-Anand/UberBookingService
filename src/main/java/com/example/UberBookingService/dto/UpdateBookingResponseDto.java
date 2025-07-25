package com.example.UberBookingService.dto;


import com.example.UberEntityService.models.BookingStatus;
import com.example.UberEntityService.models.Driver;
import lombok.*;

import java.util.Optional;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBookingResponseDto {

    private BookingStatus status;
    private Long bookingId;
    private DriverDto driver;  // ✅ replace entity with flat DTO


}
