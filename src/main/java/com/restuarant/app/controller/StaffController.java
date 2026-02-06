package com.restuarant.app.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.hibernate.sql.ast.tree.from.TableReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restuarant.app.entity.Reservation;
import com.restuarant.app.entity.TableEntity;
import com.restuarant.app.entity.TimeSlot;
import com.restuarant.app.repository.ReservationRepository;
import com.restuarant.app.repository.TableRepository;
import com.restuarant.app.repository.TimeSlotRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
public class StaffController {
	
	private final TimeSlotRepository timeSlotRepository;
    private final ReservationRepository reservationRepository;
    private final TableRepository tableRepository;

    // Fulfills: Add new available time slots [cite: 30]
    @PostMapping("/slots")
    public TimeSlot addSlot(@RequestBody TimeSlot slot) {
    	TableEntity existingTable = tableRepository.findById(slot.getTable().getId())
    	        .orElseThrow(() -> new RuntimeException("Table not found"));

    	    // 2. Attach the "Persistent" table to the slot
    	    slot.setTable(existingTable);
    	
        return timeSlotRepository.save(slot);
    }
    
    @GetMapping("/tables")
    public List<TableEntity> getAllTables() {
        return tableRepository.findAll();
    }

    // Fulfills: View all current reservations [cite: 42]
    @GetMapping("/reservations")
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }
    
 // Fulfills: Handle reservation modifications
    @DeleteMapping("/manage/cancel/{id}")
    public ResponseEntity<String> staffCancel(@PathVariable Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        
        // Free the slot
        TimeSlot slot = reservation.getTimeSlot();
        slot.setAvailable(true);
        timeSlotRepository.save(slot);
        
        reservationRepository.delete(reservation);
        return ResponseEntity.ok("Reservation removed by staff");
    }
    
 // Fulfills: Track table occupancy status
    @GetMapping("/occupancy")
    public Map<String, Long> getOccupancyStatus() {
        long booked = timeSlotRepository.countByIsAvailable(false);
        long available = timeSlotRepository.countByIsAvailable(true);
        return Map.of("booked", booked, "available", available);
    }

    // Fulfills: Generate daily booking reports
    @GetMapping("/reports/daily")
    public List<Reservation> getDailyReport() {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDate.now().atTime(LocalTime.MAX);
        return reservationRepository.findAll().stream()
                .filter(r -> r.getBookingTimestamp().isAfter(start) && 
                             r.getBookingTimestamp().isBefore(end))
                .collect(Collectors.toList());
    }

}
