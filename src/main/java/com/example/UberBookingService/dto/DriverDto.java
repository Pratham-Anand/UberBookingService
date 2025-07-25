package com.example.UberBookingService.dto;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class DriverDto {
    private Long id;
    private String name;
    private String licenseNumber;
}
