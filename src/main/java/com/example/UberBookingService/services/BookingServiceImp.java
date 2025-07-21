package com.example.UberBookingService.services;

import com.example.UberBookingService.dto.CreateBookingDto;
import com.example.UberBookingService.dto.CreateBookingResponseDto;
import com.example.UberBookingService.dto.DriverLocationDto;
import com.example.UberBookingService.dto.NearbyDriversRequestDto;
import com.example.UberBookingService.repositories.BookingRepository;
import com.example.UberBookingService.repositories.ExactLocationRepository;
import com.example.UberBookingService.repositories.PassengerRepository;
import com.example.UberEntityService.models.Booking;
import com.example.UberEntityService.models.BookingStatus;
import com.example.UberEntityService.models.ExactLocation;
import com.example.UberEntityService.models.Passenger;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Arrays;
import java.util.Optional;


@Service
public class BookingServiceImp  implements BookingService{

    private final BookingRepository bookingRepository;
    private final PassengerRepository passengerRepository;
    private final ExactLocationRepository exactLocationRepository;
    private final RestTemplate restTemplate;
    private static final String LOCATION_SERVICE = "http://localhost:8084";


    public BookingServiceImp(PassengerRepository passengerRepository, BookingRepository bookingRepository,ExactLocationRepository exactLocationRepository){
        this.passengerRepository=passengerRepository;
        this.bookingRepository=bookingRepository;
        this.exactLocationRepository=exactLocationRepository;
        this.restTemplate=new RestTemplate();
    }

    @Override
    public CreateBookingResponseDto createBooking(CreateBookingDto bookingDetails) {

        Optional<Passenger> passenger = passengerRepository.findById(bookingDetails.getPassengerId());
        ExactLocation savedStartLocation = exactLocationRepository.save(bookingDetails.getStartLocation());


        Booking booking =Booking.builder()
                .bookingstatus(BookingStatus.ASSIGNING_DRIVER)
                .startLocation(bookingDetails.getStartLocation())
                .passenger(passenger.get())
                .build();

        Booking newBooking=bookingRepository.save(booking);

        // making an api  call to fetch  nearby drivers .

        NearbyDriversRequestDto request =NearbyDriversRequestDto.builder()
                .latitude((bookingDetails.getStartLocation().getLatitude()))    //start location ka latitude and start location ka longitude;
                .longitude(bookingDetails.getStartLocation().getLongitude())
                .build();

        ResponseEntity<DriverLocationDto[]> result = restTemplate.postForEntity(LOCATION_SERVICE + "/api/location/nearby/drivers", request, DriverLocationDto[].class);

        if(result.getStatusCode().is2xxSuccessful() && result.getBody()!=null){
            List<DriverLocationDto> driverLocations=Arrays.asList(result.getBody());

            driverLocations.forEach(driverLocationDto -> {
                System.out.println(driverLocationDto.getDriverId() + " " + "lat: " + driverLocationDto.getLatitude() + "long: " + driverLocationDto.getLongitude());
            });
        }

        return CreateBookingResponseDto.builder()
                .bookingId(newBooking.getId())
                .bookingStatus(newBooking.getBookingstatus().toString())
                .build();

    }

    }


