package tn.esprit.tn.medicare_ai.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.tn.medicare_ai.dto.request.EventRequestDTO.FeedbackRequestDTO;


import tn.esprit.tn.medicare_ai.dto.response.EventResponseDTO.FeedbackResponseDTO;
import tn.esprit.tn.medicare_ai.entity.*;
import tn.esprit.tn.medicare_ai.repository.*;
import tn.esprit.tn.medicare_ai.repository.event.HealthEventRepository;
import tn.esprit.tn.medicare_ai.service.Eventinterface.Feedbackinterface;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
public class FeedbackController {


        private final Feedbackinterface feedbackService;
        private final HealthEventRepository healthEventRepository;
        private final UserRepository userRepository;

        @PostMapping
        public FeedbackResponseDTO add(@RequestBody FeedbackRequestDTO dto) {

            HealthEvent event = healthEventRepository.findById(dto.getHealthEventId()).orElse(null);
            User user = userRepository.findById(dto.getUserId()).orElse(null);

            // 🔥 Conversion manuelle DTO → Entity
            Feedback feedback = new Feedback();
            feedback.setUserName(dto.getUserName());
            feedback.setComment(dto.getComment());
            feedback.setRating(dto.getRating());
            feedback.setHealthEvent(event);
            feedback.setUser(user);

            Feedback saved = feedbackService.addFeedback(feedback);

            // 🔥 Conversion Entity → DTO
            FeedbackResponseDTO res = new FeedbackResponseDTO();
            res.setId(saved.getId());
            res.setUserName(saved.getUserName());
            res.setComment(saved.getComment());
            res.setRating(saved.getRating());
            res.setCreatedAt(saved.getCreatedAt());

            if (saved.getHealthEvent() != null) {
                res.setHealthEventId(saved.getHealthEvent().getId());
                res.setHealthEventTitle(saved.getHealthEvent().getTitle());
            }

            if (saved.getUser() != null) {
                res.setUserId(saved.getUser().getId());
            }

            return res;
        }

        @GetMapping
        public List<FeedbackResponseDTO> getAll() {
            return feedbackService.getAllFeedbacks()
                    .stream()
                    .map(f -> {
                        FeedbackResponseDTO dto = new FeedbackResponseDTO();
                        dto.setId(f.getId());
                        dto.setUserName(f.getUserName());
                        dto.setComment(f.getComment());
                        dto.setRating(f.getRating());
                        dto.setCreatedAt(f.getCreatedAt());

                        if (f.getHealthEvent() != null) {
                            dto.setHealthEventId(f.getHealthEvent().getId());
                            dto.setHealthEventTitle(f.getHealthEvent().getTitle());
                        }

                        if (f.getUser() != null) {
                            dto.setUserId(f.getUser().getId());
                        }

                        return dto;
                    })
                    .collect(Collectors.toList());
        }

        @DeleteMapping("/{id}")
        public void delete(@PathVariable Long id) {
            feedbackService.deleteFeedback(id);
        }
    @PutMapping("/{id}")
    public FeedbackResponseDTO update(@PathVariable Long id, @RequestBody FeedbackRequestDTO dto) {

        Feedback existing = feedbackService.getAllFeedbacks()
                .stream()
                .filter(f -> f.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (existing == null) return null;

        // 🔥 update direct
        existing.setUserName(dto.getUserName());
        existing.setComment(dto.getComment());
        existing.setRating(dto.getRating());

        Feedback saved = feedbackService.addFeedback(existing);

        // 🔥 response
        FeedbackResponseDTO res = new FeedbackResponseDTO();
        res.setId(saved.getId());
        res.setUserName(saved.getUserName());
        res.setComment(saved.getComment());
        res.setRating(saved.getRating());
        res.setCreatedAt(saved.getCreatedAt());

        if (saved.getHealthEvent() != null) {
            res.setHealthEventId(saved.getHealthEvent().getId());
            res.setHealthEventTitle(saved.getHealthEvent().getTitle());
        }

        if (saved.getUser() != null) {
            res.setUserId(saved.getUser().getId());
        }

        return res;
    }
    }

