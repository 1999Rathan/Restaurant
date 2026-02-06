package com.restuarant.app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "tables")
public class TableEntity {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Integer capacity; // 2, 4, 6 person [cite: 11]
    private String area; // Indoor, Outdoor, Private [cite: 11]
    private String specialFeatures; // Window view, Quiet area [cite: 12]

}
