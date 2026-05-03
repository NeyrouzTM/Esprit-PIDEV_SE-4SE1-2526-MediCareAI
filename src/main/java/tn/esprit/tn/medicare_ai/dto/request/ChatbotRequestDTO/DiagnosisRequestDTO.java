package tn.esprit.tn.medicare_ai.dto.request.ChatbotRequestDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiagnosisRequestDTO {
    private List<String> symptomNames;
}
