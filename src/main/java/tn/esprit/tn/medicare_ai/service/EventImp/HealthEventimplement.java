package tn.esprit.tn.medicare_ai.service.EventImp;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.tn.medicare_ai.entity.HealthEvent;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.repository.event.HealthEventRepository;
import tn.esprit.tn.medicare_ai.service.Eventinterface.HealthEventinterface;

import java.util.List;
@Service
@RequiredArgsConstructor

public class HealthEventimplement implements HealthEventinterface {


        private final HealthEventRepository healthEventRepository;

        @Override
        public HealthEvent addEvent(HealthEvent event) {
            return healthEventRepository.save(event);
        }

        @Override
        public List<HealthEvent> getAllEvents() {
            return healthEventRepository.findAll();
        }

        @Override
        public HealthEvent getEventById(Long id) {
            return healthEventRepository.findById(id).orElse(null);
        }

        @Override
        public void deleteEvent(Long id) {
            healthEventRepository.deleteById(id);
        }



    @Override
    public HealthEvent updateEvent(Long id, HealthEvent updatedEvent) {
        return healthEventRepository.findById(id).map(event -> {
            event.setTitle(updatedEvent.getTitle());
            event.setCategory(updatedEvent.getCategory());
            event.setDescription(updatedEvent.getDescription());
            event.setEventDate(updatedEvent.getEventDate());
            event.setLocation(updatedEvent.getLocation());
            return healthEventRepository.save(event);
        }).orElse(null);
    }

    @Override
    public HealthEvent addParticipant(Long eventId, User user) {
        return healthEventRepository.findById(eventId).map(event -> {
            event.getParticipants().add(user);
            return healthEventRepository.save(event);
        }).orElse(null);
    }

    }

