package tn.esprit.tn.medicare_ai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.tn.medicare_ai.dto.response.AiNutritionResponseDTO;
import tn.esprit.tn.medicare_ai.exception.AiNutritionBadRequestException;
import tn.esprit.tn.medicare_ai.service.AiNutritionService;

@RestController
@RequestMapping("/api/nutrition")
@RequiredArgsConstructor
@Tag(name = "AI Nutrition", description = "Food recognition and calorie estimation using an external AI service")
public class AiNutritionController {

    private final AiNutritionService aiNutritionService;

    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Analyze a food image")
    public ResponseEntity<AiNutritionResponseDTO> analyze(
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        MultipartFile uploadedImage = image != null ? image : file;
        if (uploadedImage == null) {
            throw new AiNutritionBadRequestException("A multipart file part named 'image' or 'file' is required");
        }
        return ResponseEntity.ok(aiNutritionService.analyze(uploadedImage));
    }
}