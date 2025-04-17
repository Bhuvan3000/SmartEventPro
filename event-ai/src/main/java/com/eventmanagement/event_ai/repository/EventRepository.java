package com.eventmanagement.event_ai.repository;


import com.eventmanagement.event_ai.model.Event;
import com.eventmanagement.event_ai.model.EventStatus; 
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface EventRepository extends MongoRepository<Event, String> {
    List<Event> findByOrganizerId(String organizerId);
    List<Event> findByStatus(EventStatus status);
    List<Event> findByTitleContainingIgnoreCase(String title);
    List<Event> findByAttendeeIdsContaining(String attendeeId);
}