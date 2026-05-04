package tn.esprit.tn.medicare_ai.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.tn.medicare_ai.dto.request.ChatbotRequestDTO.DiagnosisRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.chatbotResponseDTO.DiseaseResponseDTO;
import tn.esprit.tn.medicare_ai.dto.response.chatbotResponseDTO.SpecialtyResponseDTO;
import tn.esprit.tn.medicare_ai.service.chatbotinterface.ChatbotInterface;

import java.util.List;

@RestController
@RequestMapping("/chatbot")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatbotController {

    private final ChatbotInterface chatbotService;

    @PostMapping("/doctor/diagnose")
    public ResponseEntity<List<DiseaseResponseDTO>> diagnoseForDoctor(@RequestBody DiagnosisRequestDTO request) {
        return ResponseEntity.ok(chatbotService.diagnoseForDoctor(request));
    }

    @PostMapping("/patient/diagnose")
    public ResponseEntity<List<SpecialtyResponseDTO>> diagnoseForPatient(@RequestBody DiagnosisRequestDTO request) {
        return ResponseEntity.ok(chatbotService.diagnoseForPatient(request));
    }
}
