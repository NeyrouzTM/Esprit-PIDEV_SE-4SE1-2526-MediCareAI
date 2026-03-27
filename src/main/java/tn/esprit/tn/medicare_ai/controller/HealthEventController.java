package tn.esprit.tn.medicare_ai.controller;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.tn.medicare_ai.dto.*;
import tn.esprit.tn.medicare_ai.dto.request.EventRequestDTO.HealthEventRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.EventResponseDTO.HealthEventResponseDTO;
import tn.esprit.tn.medicare_ai.entity.*;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.repository.event.HealthEventRepository;
import tn.esprit.tn.medicare_ai.service.Eventinterface.HealthEventinterface;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor

public class HealthEventController {

        private final HealthEventRepository healthEventRepository;

        private final UserRepository userRepository;
        private final HealthEventinterface healthEventService;

        @PostMapping
        public HealthEventResponseDTO add(@RequestBody HealthEventRequestDTO dto) {

            // 🔥 DTO → Entity
            HealthEvent event = new HealthEvent();
            event.setTitle(dto.getTitle());
            event.setCategory(EventCategory.valueOf(dto.getCategory()));
            event.setDescription(dto.getDescription());
            event.setEventDate(dto.getEventDate());
            event.setLocation(dto.getLocation());

            HealthEvent saved = healthEventService.addEvent(event);

            // 🔥 Entity → DTO
            HealthEventResponseDTO res = new HealthEventResponseDTO();
            res.setId(saved.getId());
            res.setTitle(saved.getTitle());
            res.setCategory(saved.getCategory().name());
            res.setDescription(saved.getDescription());
            res.setEventDate(saved.getEventDate());
            res.setLocation(saved.getLocation());

            return res;
        }

        @GetMapping
        public List<HealthEventResponseDTO> getAll() {
            return healthEventService.getAllEvents()
                    .stream()
                    .map(e -> {
                        HealthEventResponseDTO dto = new HealthEventResponseDTO();
                        dto.setId(e.getId());
                        dto.setTitle(e.getTitle());
                        dto.setCategory(e.getCategory().name());
                        dto.setDescription(e.getDescription());
                        dto.setEventDate(e.getEventDate());
                        dto.setLocation(e.getLocation());
                        return dto;
                    })
                    .collect(Collectors.toList());
        }

    @Transactional
    @DeleteMapping("/{id}")

    public void deleteEvent(Long id) {

        HealthEvent event = healthEventRepository.findById(id).orElse(null);

        if (event != null) {

            // 🔥 détacher tous les users
            for (User user : event.getParticipants()) {
                user.getEvents().remove(event);  // côté user
            }

            event.getParticipants().clear(); // côté event

            // 🔥 maintenant suppression safe
            healthEventRepository.delete(event);
        }
    }
    @PutMapping("/{id}")
    public HealthEventResponseDTO update(@PathVariable Long id, @RequestBody HealthEventRequestDTO dto) {

        HealthEvent event = healthEventService.getEventById(id);

        if (event == null) return null;

        // 🔥 update direct
        event.setTitle(dto.getTitle());
        event.setCategory(EventCategory.valueOf(dto.getCategory()));
        event.setDescription(dto.getDescription());
        event.setEventDate(dto.getEventDate());
        event.setLocation(dto.getLocation());

        HealthEvent saved = healthEventService.addEvent(event);

        // 🔥 response
        HealthEventResponseDTO res = new HealthEventResponseDTO();
        res.setId(saved.getId());
        res.setTitle(saved.getTitle());
        res.setCategory(saved.getCategory().name());
        res.setDescription(saved.getDescription());
        res.setEventDate(saved.getEventDate());
        res.setLocation(saved.getLocation());

        return res;
    }
    @PostMapping("/{eventId}/participants/{userId}")
    public HealthEventResponseDTO addParticipant(@PathVariable Long eventId,
                                                 @PathVariable Long userId) {

        HealthEvent event = healthEventService.getEventById(eventId);
        User user = userRepository.findById(userId).orElse(null);

        if (event == null || user == null) return null;

        // 🔥 Synchronisation des deux côtés
        event.getParticipants().add(user);
        user.getEvents().add(event);

        healthEventService.addEvent(event);

        // réponse
        HealthEventResponseDTO res = new HealthEventResponseDTO();
        res.setId(event.getId());
        res.setTitle(event.getTitle());
        res.setCategory(event.getCategory().name());
        res.setDescription(event.getDescription());
        res.setEventDate(event.getEventDate());
        res.setLocation(event.getLocation());

        return res;
    }
    }

