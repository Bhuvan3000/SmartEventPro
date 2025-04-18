package com.eventmanagement.event_ai.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "events")
@Data
public class Event {
    @Id
    private String id;
    private String title;
    private String description;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String location;
    private String organizerId;
    private List<String> attendeeIds;
    private EventStatus status; // Enum: PLANNED, ONGOING, COMPLETED, CANCELLED
    private List<String> tags;
    private Double aiRating; // AI-generated rating for the event
    private String aiSummary; // AI-generated summary of the event
}