package com.example.UberBookingService.controller;

import com.example.UberBookingService.dto.CreateBookingDto;
import com.example.UberBookingService.dto.CreateBookingResponseDto;
import com.example.UberBookingService.dto.UpdateBookingRequestDto;
import com.example.UberBookingService.dto.UpdateBookingResponseDto;
import com.example.UberBookingService.services.BookingService;
import jakarta.ws.rs.Path;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/booking")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService){
        this.bookingService=bookingService;
    }

    @PostMapping
    public ResponseEntity<CreateBookingResponseDto> createBooking(@RequestBody CreateBookingDto createBookingDto){
        return new ResponseEntity<>(bookingService.createBooking(createBookingDto), HttpStatus.CREATED);
    }

//    @PatchMapping("/{bookingId}")
    @PostMapping("/{bookingId}")       //client socket i.e driver will be sending the data to this so did post mapping,
    public ResponseEntity<UpdateBookingResponseDto>updateBooking(@RequestBody UpdateBookingRequestDto requestDto , @PathVariable long bookingId){
        return new ResponseEntity<>(bookingService.updateBooking(requestDto,bookingId),HttpStatus.OK);

    }
}
