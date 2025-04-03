package com.SmartEventPro.eventmanagement.repository;

import com.SmartEventPro.eventmanagement.model.Attendee;
import com.SmartEventPro.eventmanagement.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Set;

public interface AttendeeRepository extends JpaRepository<Attendee, Long> {

    // Find an attendee by their associated user ID
	Attendee findByUserId(Long userId);

    // Find all attendees who have registered for a specific event
    @Query("SELECT a FROM Attendee a JOIN a.events e WHERE e.id = :eventId")
    List<Attendee> findAttendeesByEventId(@Param("eventId") Long eventId);

    // Find all events attended by a specific attendee
    @Query("SELECT e FROM Event e JOIN e.attendees a WHERE a.id = :attendeeId")
    Set<Event> findEventsByAttendeeId(@Param("attendeeId") Long attendeeId);
}