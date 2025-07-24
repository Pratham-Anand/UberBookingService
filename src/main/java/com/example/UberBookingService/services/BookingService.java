package com.example.UberBookingService.services;


import com.example.UberBookingService.dto.CreateBookingDto;
import com.example.UberBookingService.dto.CreateBookingResponseDto;
import com.example.UberBookingService.dto.UpdateBookingRequestDto;
import com.example.UberBookingService.dto.UpdateBookingResponseDto;

public interface BookingService {

    CreateBookingResponseDto createBooking(CreateBookingDto createBookingDto);
    UpdateBookingResponseDto updateBooking(UpdateBookingRequestDto bookingRequestDto,Long bookingId);


}
