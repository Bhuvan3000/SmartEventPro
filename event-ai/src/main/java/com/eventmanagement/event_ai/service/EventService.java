package com.eventmanagement.event_ai.service;

import com.eventmanagement.event_ai.exception.GeminiAPIException;
import com.eventmanagement.event_ai.model.Event;
import com.eventmanagement.event_ai.model.EventStatus;
import com.eventmanagement.event_ai.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {
    private static final Logger logger = LoggerFactory.getLogger(EventService.class); // Added logger
    private final EventRepository eventRepository;
    private final AIService aiService; // Added AIService dependency

    public EventService(EventRepository eventRepository, AIService aiService) {
        this.eventRepository = eventRepository;
        this.aiService = aiService;
    }

    public Event createEvent(Event event) {
        try {
            // Generate AI summary and rating
            String aiSummary = aiService.generateEventSummary(event);
            Double aiRating = aiService.rateEventQuality(event);

            event.setAiSummary(aiSummary);
            event.setAiRating(aiRating);
        } catch (GeminiAPIException e) {
            // Log the error but continue with default values
            logger.error("AI service failed, using default values", e);
            event.setAiSummary("Summary unavailable - AI service error");
            event.setAiRating(5.0);
        }

        return eventRepository.save(event);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Optional<Event> getEventById(String id) {
        return eventRepository.findById(id);
    }

    public Event updateEvent(String id, Event eventDetails) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found")); // Replace with a custom exception if needed

        // Ensure these methods exist in the Event class
        event.setTitle(eventDetails.getTitle());
        event.setDescription(eventDetails.getDescription());
        // Update other fields as needed

        return eventRepository.save(event);
    }

    public void deleteEvent(String id) {
        eventRepository.deleteById(id);
    }

    public List<Event> getEventsByOrganizer(String organizerId) {
        return eventRepository.findByOrganizerId(organizerId);
    }

    public List<Event> getUpcomingEvents() {
        // Implement logic to get upcoming events
        // This is a placeholder - you'd need to add date comparison logic
        return eventRepository.findByStatus(EventStatus.PLANNED);
    }
}