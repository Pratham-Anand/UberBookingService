package com.example.UberBookingService.repositories;


import com.example.UberEntityService.models.Booking;
import com.example.UberEntityService.models.BookingStatus;
import com.example.UberEntityService.models.Driver;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingRepository extends JpaRepository<Booking,Long> {

    @Modifying
    @Transactional
    @Query("Update Booking b SET b.bookingstatus=:status ,b.driver=:driver where b.id=:id")
   void updateBookingStatusAndDriverById(@Param("id") Long id, @Param("status") BookingStatus status ,@Param("driver") Driver driver);

}
