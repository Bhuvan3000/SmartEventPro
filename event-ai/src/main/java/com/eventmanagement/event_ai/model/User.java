package com.eventmanagement.event_ai.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "users")
@Data
public class User {
    @Id
    private String id;
    private String username;
    private String email;
    private String password; // In real app, this should be encrypted
    private UserRole role; // Enum: ADMIN, ORGANIZER, ATTENDEE
    private List<String> eventIds; // Events they're organizing or attending
}