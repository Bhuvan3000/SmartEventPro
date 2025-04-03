package com.SmartEventPro.eventmanagement.repository;

import com.SmartEventPro.eventmanagement.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    // Find all events after a specific date
    List<Event> findByDateTimeAfter(LocalDateTime dateTime);

    // Find events by name (case-insensitive)
    List<Event> findByNameContainingIgnoreCase(String name);
}
