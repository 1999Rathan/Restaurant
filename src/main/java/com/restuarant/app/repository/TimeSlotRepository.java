package com.restuarant.app.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.restuarant.app.entity.TimeSlot;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
	
	// Fulfills "Display available time slots" [cite: 8]
    List<TimeSlot> findByIsAvailableTrue();
    
    long countByIsAvailable(boolean isAvailable);
    
    // Fulfills "Search reservations by Date and time" [cite: 15, 17]
    List<TimeSlot> findByStartTimeBetweenAndIsAvailableTrue(LocalDateTime start, LocalDateTime end);

}
