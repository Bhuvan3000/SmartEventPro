package com.SmartEventPro.eventmanagement.repository;

import com.SmartEventPro.eventmanagement.model.Organizer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface OrganizerRepository extends JpaRepository<Organizer, Long> {

    // Find an organizer by their associated user ID
    Organizer findByUserId(Long userId);

    // Find all organizers who have created events after a specific date
    @Query("SELECT o FROM Organizer o JOIN o.events e WHERE e.dateTime > :dateTime")
    List<Organizer> findOrganizersWithEventsAfter(@Param("dateTime") LocalDateTime dateTime);

    // Count the number of events organized by a specific organizer
    @Query("SELECT COUNT(e) FROM Event e WHERE e.organizer.id = :organizerId")
    Long countEventsByOrganizerId(@Param("organizerId") Long organizerId);
}
