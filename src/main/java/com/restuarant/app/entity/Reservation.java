package com.restuarant.app.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "reservations")
public class Reservation {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String userId; // Store the Keycloak Sub/UUID
    
    @OneToOne
    private TimeSlot timeSlot;
    
    private Integer partySize; //[cite: 25]
    private LocalDateTime bookingTimestamp;
    private String specialRequests; //[cite: 50]

}
