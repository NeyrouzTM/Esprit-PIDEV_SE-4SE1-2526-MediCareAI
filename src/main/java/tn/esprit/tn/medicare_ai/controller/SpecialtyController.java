package tn.esprit.tn.medicare_ai.controller;



import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import tn.esprit.tn.medicare_ai.dto.request.ChatbotRequestDTO.SpecialtyRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.chatbotResponseDTO.SpecialtyResponseDTO;
import tn.esprit.tn.medicare_ai.service.chatbotinterface.SpecialtyInterface;

import java.util.List;

@RestController
@RequestMapping( "/specialties")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SpecialtyController {

    private final SpecialtyInterface specialtyService;

    @PostMapping
    public ResponseEntity<SpecialtyResponseDTO> create(@Valid @RequestBody SpecialtyRequestDTO dto) {
        return ResponseEntity.ok(specialtyService.createSpecialty(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SpecialtyResponseDTO> update(@PathVariable Long id,
                                                       @Valid @RequestBody SpecialtyRequestDTO dto) {
        return ResponseEntity.ok(specialtyService.updateSpecialty(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        specialtyService.deleteSpecialty(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpecialtyResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(specialtyService.getSpecialty(id));
    }

    @GetMapping
    public ResponseEntity<List<SpecialtyResponseDTO>> getAll() {
        return ResponseEntity.ok(specialtyService.getAllSpecialties());
    }
}
