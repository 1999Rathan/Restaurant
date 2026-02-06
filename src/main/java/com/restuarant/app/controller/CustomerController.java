package com.restuarant.app.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties.Jwt;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.restuarant.app.entity.Reservation;
import com.restuarant.app.entity.TimeSlot;
import com.restuarant.app.repository.TimeSlotRepository;
import com.restuarant.app.service.ReservationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {
	
	private final TimeSlotRepository timeSlotRepository;
    private final ReservationService reservationService;

    // Fulfills: Display available time slots on the home page [cite: 8]
    @GetMapping("/slots")
    public List<TimeSlot> getAvailableSlots() {
        return timeSlotRepository.findByIsAvailableTrue();
    }

    // Fulfills: Book tables for specific date/time [cite: 25]
    @PostMapping("/reserve/{slotId}")
    public ResponseEntity<Reservation> bookTable(
            @PathVariable Long slotId, 
            @RequestBody Reservation reservation,
            @AuthenticationPrincipal Jwt jwt) {
        
        // Use the 'sub' claim from Keycloak JWT as the unique User ID [cite: 59]
        reservation.setUserId(jwt.getSubject()); 
        return ResponseEntity.ok(reservationService.createReservation(reservation, slotId));
    }
    
 // Fulfills: Search reservations by Date, Time, and Party Size [cite: 17, 18]
    @GetMapping("/search")
    public List<TimeSlot> searchSlots(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam Integer partySize) {
        return timeSlotRepository.findByIsAvailableTrue().stream() // Ensure .stream() is here
                .filter(slot -> slot.getStartTime().isAfter(start))
                // If capacity is in TableEntity, use slot.getTable().getCapacity()
                .filter(slot -> slot.getTable().getCapacity() >= partySize) 
                .collect(Collectors.toList());
    }

    // Fulfills: View personal reservation history 
    @GetMapping("/my-history")
    public List<Reservation> getMyHistory(@AuthenticationPrincipal Jwt jwt) {
        return reservationService.findByUserId(jwt.getSubject());
    }
    
 // Fulfills: Modify or cancel existing reservations (with restrictions)
    @DeleteMapping("/cancel/{id}")
    public ResponseEntity<String> cancel(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        reservationService.cancelReservation(id, jwt.getSubject());
        return ResponseEntity.ok("Reservation cancelled successfully");
    }

}
