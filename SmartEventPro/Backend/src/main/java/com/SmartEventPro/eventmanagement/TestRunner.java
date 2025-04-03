package com.SmartEventPro.eventmanagement;

import com.SmartEventPro.eventmanagement.model.Event;
import com.SmartEventPro.eventmanagement.model.Organizer;
import com.SmartEventPro.eventmanagement.model.Attendee;
import com.SmartEventPro.eventmanagement.model.User;
import com.SmartEventPro.eventmanagement.repository.OrganizerRepository;
import com.SmartEventPro.eventmanagement.repository.AttendeeRepository;
import com.SmartEventPro.eventmanagement.repository.EventRepository;
import com.SmartEventPro.eventmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Component
public class TestRunner implements CommandLineRunner {

    @Autowired
    private OrganizerRepository organizerRepository;

    @Autowired
    private AttendeeRepository attendeeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Override
    public void run(String... args) throws Exception {
        // Create test data
        User user1 = new User();
        user1.setUsername("organizer1");
        user1.setPassword("password123");
        user1.setEmail("organizer1@example.com");
        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("attendee1");
        user2.setPassword("password123");
        user2.setEmail("attendee1@example.com");
        userRepository.save(user2);

        Organizer organizer = new Organizer();
        organizer.setUser(user1);
        organizerRepository.save(organizer);

        Attendee attendee = new Attendee();
        attendee.setUser(user2);
        attendeeRepository.save(attendee);

        Event event = new Event();
        event.setName("Spring Boot Workshop");
        event.setDescription("Learn Spring Boot from scratch.");
        event.setDateTime(LocalDateTime.now());
        event.setVenue("Online");
        event.setOrganizer(organizer);
        event.getAttendees().add(attendee);
        eventRepository.save(event);

        // Test OrganizerRepository queries
        Organizer foundOrganizer = organizerRepository.findByUserId(user1.getId());
        System.out.println("Organizer by User ID: " + foundOrganizer);

        List<Organizer> organizersWithEvents = organizerRepository.findOrganizersWithEventsAfter(LocalDateTime.now().minusDays(1));
        System.out.println("Organizers with events after yesterday: " + organizersWithEvents);

        Long eventCount = organizerRepository.countEventsByOrganizerId(organizer.getId());
        System.out.println("Number of events by Organizer ID: " + eventCount);

        // Test AttendeeRepository queries
        Attendee foundAttendee = attendeeRepository.findByUserId(user2.getId());
        System.out.println("Attendee by User ID: " + foundAttendee);

        List<Attendee> attendeesForEvent = attendeeRepository.findAttendeesByEventId(event.getId());
        System.out.println("Attendees for Event ID: " + attendeesForEvent);

        Set<Event> eventsForAttendee = attendeeRepository.findEventsByAttendeeId(attendee.getId());
        System.out.println("Events attended by Attendee ID: " + eventsForAttendee);
    }
}