package com.restuarant.app.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "time_slots")
public class TimeSlot {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    private TableEntity table;
    
    private LocalDateTime startTime; //[cite: 31]
    private LocalDateTime endTime; //[cite: 39]
    private Double pricePerPerson; //[cite: 38]
    private boolean isAvailable = true; // To block booked slots [cite: 26]

}
