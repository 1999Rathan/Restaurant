package com.restuarant.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.restuarant.app.entity.Reservation;


@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
	
	// Fulfills "View personal reservation history" [cite: 26]
    List<Reservation> findByUserId(String userId);

}
