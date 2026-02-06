package com.restuarant.app.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.restuarant.app.entity.Reservation;
import com.restuarant.app.entity.TimeSlot;
import com.restuarant.app.repository.ReservationRepository;
import com.restuarant.app.repository.TimeSlotRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationService {
	
	private final ReservationRepository reservationRepository;
    private final TimeSlotRepository timeSlotRepository;

    @Transactional
    public Reservation createReservation(Reservation request, Long slotId) {
        // 1. Find the slot
        TimeSlot slot = timeSlotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Slot not found")); //[cite: 79]

        // 2. Conflict Prevention: Check if already booked 
        if (!slot.isAvailable()) {
            throw new RuntimeException("This table is already booked for this time!"); //[cite: 79]
        }

        // 3. Block the slot [cite: 26]
        slot.setAvailable(false);
        timeSlotRepository.save(slot);

        // 4. Save the reservation
        request.setTimeSlot(slot);
        request.setBookingTimestamp(LocalDateTime.now());
        return reservationRepository.save(request);
    }

	public List<Reservation> findByUserId(String subject) {
		
		return reservationRepository.findByUserId(subject);
	}
	
	@Transactional
	public void cancelReservation(Long reservationId, String userId) {
	    // 1. Find the reservation
	    Reservation reservation = reservationRepository.findById(reservationId)
	            .orElseThrow(() -> new RuntimeException("Reservation not found"));

	    // 2. Restriction: Only the owner can cancel their own reservation
	    if (!reservation.getUserId().equals(userId)) {
	        throw new RuntimeException("Unauthorized to cancel this reservation");
	    }

	    // 3. Restriction: Cannot cancel past reservations
	    if (reservation.getTimeSlot().getStartTime().isBefore(LocalDateTime.now())) {
	        throw new RuntimeException("Cannot cancel a reservation that has already started or passed");
	    }

	    // 4. Free up the TimeSlot
	    TimeSlot slot = reservation.getTimeSlot();
	    slot.setAvailable(true);
	    timeSlotRepository.save(slot);

	    // 5. Delete or Update status
	    reservationRepository.delete(reservation); 
	}

}
