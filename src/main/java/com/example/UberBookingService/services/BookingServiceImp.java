package com.example.UberBookingService.services;

import com.example.UberBookingService.apis.LocationServiceApi;
import com.example.UberBookingService.apis.UberSocketApi;
import com.example.UberBookingService.dto.*;
import com.example.UberBookingService.repositories.BookingRepository;
import com.example.UberBookingService.repositories.DriverRepository;
import com.example.UberBookingService.repositories.ExactLocationRepository;
import com.example.UberBookingService.repositories.PassengerRepository;
import com.example.UberEntityService.models.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;
import java.util.Arrays;
import java.util.Optional;


@Service
public class BookingServiceImp  implements BookingService{

    private final BookingRepository bookingRepository;
    private final PassengerRepository passengerRepository;
    private final ExactLocationRepository exactLocationRepository;
    private final RestTemplate restTemplate;
//    private static final String LOCATION_SERVICE = "http://localhost:8084";
    private final DriverRepository driverRepository;
    private final LocationServiceApi locationServiceApi;
    private final UberSocketApi uberSocketApi;


    public BookingServiceImp(PassengerRepository passengerRepository, BookingRepository bookingRepository, ExactLocationRepository exactLocationRepository, LocationServiceApi locationServiceApi, DriverRepository driverRepository,UberSocketApi uberSocketApi){
        this.passengerRepository=passengerRepository;
        this.bookingRepository=bookingRepository;
        this.exactLocationRepository=exactLocationRepository;
        this.restTemplate=new RestTemplate();
        this.locationServiceApi=locationServiceApi;
        this.driverRepository=driverRepository;
        this.uberSocketApi=uberSocketApi;
    }

    @Override
    public CreateBookingResponseDto createBooking(CreateBookingDto bookingDetails) {

        Optional<Passenger> passenger = passengerRepository.findById(bookingDetails.getPassengerId());
        ExactLocation savedStartLocation = exactLocationRepository.save(bookingDetails.getStartLocation()); //or make the mapping .persist or all


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
//
//        ResponseEntity<DriverLocationDto[]> result = restTemplate.postForEntity(LOCATION_SERVICE + "/api/location/nearby/drivers", request, DriverLocationDto[].class);
//
//        if(result.getStatusCode().is2xxSuccessful() && result.getBody()!=null){
//            List<DriverLocationDto> driverLocations=Arrays.asList(result.getBody());
//
//            driverLocations.forEach(driverLocationDto -> {
//                System.out.println(driverLocationDto.getDriverId() + " " + "lat: " + driverLocationDto.getLatitude() + "long: " + driverLocationDto.getLongitude());
//            });
//        }

        processNearbyDriversAsync(request, bookingDetails.getPassengerId(), newBooking.getId());  //nearby drivers ko find kro(jo ki ab ho rha hai) and then unko new booking request send kro so async is a better way.
        return CreateBookingResponseDto.builder()
                .bookingId(newBooking.getId())
                .bookingStatus(newBooking.getBookingstatus().toString())
                .build();

    }


    private void processNearbyDriversAsync(NearbyDriversRequestDto requestDto,Long passengerId,Long bookingId)  {
        Call<DriverLocationDto[]> call=locationServiceApi.getNearbyDrivers(requestDto);
        System.out.println(call.request().url() + " " + call.request().method() + " " + call.request().headers());
        call.enqueue(new Callback<DriverLocationDto[]>() {
            @Override
            public void onResponse(Call<DriverLocationDto[]> call, Response<DriverLocationDto[]> response) {
                if(response.isSuccessful() && response.body() != null) {
                    List<DriverLocationDto> driverLocations = Arrays.asList(response.body());
                    driverLocations.forEach(driverLocationDto -> {
                        System.out.println(driverLocationDto.getDriverId() + " " + "lat: " + driverLocationDto.getLatitude() + "long: " + driverLocationDto.getLongitude());
                    });

                    try{
                        raiseRideRequestAsync(RideRequestDto.builder().passengerId(passengerId).bookingId(bookingId).build());
                    }
                    catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    System.out.println("Request failed" + response.message());
                }

            }

            @Override
            public void onFailure(Call<DriverLocationDto[]> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void raiseRideRequestAsync(RideRequestDto rideRequestDto) throws IOException{
        Call<Boolean> call= uberSocketApi.raiseRideRequest(rideRequestDto);
        System.out.println(call.request().url() + " " + call.request().method() + " " + call.request().headers());
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                System.out.println(response.isSuccessful());
                System.out.println(response.message());
                if (response.isSuccessful() && response.body() != null) {
                    Boolean result = response.body();
                    System.out.println("Driver response is" + result.toString());
            } else {
                    System.out.println("Request for ride failed" + response.message());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable throwable) {

            }
        });
    }

    @Override
    public UpdateBookingResponseDto updateBooking(UpdateBookingRequestDto bookingRequestDto, Long bookingId) {
        System.out.println(bookingRequestDto.getDriverId().get());
        Optional<Driver> driver =driverRepository.findById(bookingRequestDto.getDriverId().get());
          //TODO : if(driver.isPresent() && driver.get().isAvailable())
        bookingRepository.updateBookingStatusAndDriverById(bookingId,BookingStatus.SCHEDULED,driver.get());  //objects are being sent instead of the table values.
        // TODO: driverRepository.update -> make it unavailable
        Optional<Booking> booking=bookingRepository.findById(bookingId);

        return UpdateBookingResponseDto.builder()
                .bookingId(bookingId)
                .status(booking.get().getBookingstatus())
                .driver(Optional.ofNullable(booking.get().getDriver()))
                .build();
    }

    }


