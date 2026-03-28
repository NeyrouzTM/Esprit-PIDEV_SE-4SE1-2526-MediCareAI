package tn.esprit.tn.medicare_ai.service.Eventinterface;

import tn.esprit.tn.medicare_ai.entity.HealthEvent;
import tn.esprit.tn.medicare_ai.entity.User;

import java.util.List;

public interface HealthEventinterface {
    HealthEvent addEvent(HealthEvent event);

    List<HealthEvent> getAllEvents();

    HealthEvent getEventById(Long id);

    void deleteEvent(Long id);

    HealthEvent updateEvent(Long id, HealthEvent updatedEvent); // update
    HealthEvent addParticipant(Long eventId, User user);       // add participant
}
