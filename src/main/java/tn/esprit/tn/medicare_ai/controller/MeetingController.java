package tn.esprit.tn.medicare_ai.controller;



import tn.esprit.tn.medicare_ai.dto.request.MeetingRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.MeetingResponseDTO;
import tn.esprit.tn.medicare_ai.service.interfaces.MeetingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meetings")
public class MeetingController {

    private final MeetingService meetingService;

    public MeetingController(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    @PostMapping
    public ResponseEntity<MeetingResponseDTO> createMeeting(
            @Valid @RequestBody MeetingRequestDTO dto,
            @RequestParam Long organizerId) {

        MeetingResponseDTO created = meetingService.createMeeting(dto, organizerId);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping
    public ResponseEntity<List<MeetingResponseDTO>> getAllMeetings() {
        return ResponseEntity.ok(meetingService.getAllMeetings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MeetingResponseDTO> getMeetingById(@PathVariable Long id) {
        return ResponseEntity.ok(meetingService.getMeetingById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MeetingResponseDTO> updateMeeting(
            @PathVariable Long id,
            @Valid @RequestBody MeetingRequestDTO dto,
            @RequestParam Long organizerId) {

        return ResponseEntity.ok(meetingService.updateMeeting(id, dto, organizerId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeeting(
            @PathVariable Long id,
            @RequestParam Long organizerId) {

        meetingService.deleteMeeting(id, organizerId);
        return ResponseEntity.noContent().build();
    }
}
