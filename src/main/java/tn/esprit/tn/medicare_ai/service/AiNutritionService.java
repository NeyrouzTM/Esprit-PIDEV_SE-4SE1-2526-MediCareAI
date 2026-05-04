package tn.esprit.tn.medicare_ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.tn.medicare_ai.dto.response.AiNutritionResponseDTO;
import tn.esprit.tn.medicare_ai.exception.AiNutritionBadRequestException;
import tn.esprit.tn.medicare_ai.exception.AiNutritionServiceUnavailableException;
import tn.esprit.tn.medicare_ai.exception.AiNutritionTimeoutException;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AiNutritionService {

    private final RestTemplate restTemplate;

    @Value("${ai.nutrition.endpoint}")
    private String aiNutritionEndpoint;

    public AiNutritionResponseDTO analyze(MultipartFile image) {
        validateImage(image);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", toMultipartResource(image));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            AiNutritionResponseDTO response = restTemplate.postForObject(aiNutritionEndpoint, requestEntity, AiNutritionResponseDTO.class);
            if (response == null) {
                throw new AiNutritionServiceUnavailableException("AI nutrition service returned an empty response");
            }
            return response;
        } catch (ResourceAccessException ex) {
            if (isTimeout(ex)) {
                throw new AiNutritionTimeoutException("AI nutrition service timed out");
            }
            throw new AiNutritionServiceUnavailableException("AI nutrition service is unavailable");
        } catch (HttpStatusCodeException ex) {
            if (ex.getStatusCode().is4xxClientError()) {
                throw new AiNutritionBadRequestException("Invalid image or unsupported content for nutrition analysis");
            }
            throw new AiNutritionServiceUnavailableException("AI nutrition service returned an error");
        }
    }

    private void validateImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new AiNutritionBadRequestException("Image is required");
        }
        String contentType = image.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new AiNutritionBadRequestException("Only image files are allowed");
        }
    }

    private ByteArrayResource toMultipartResource(MultipartFile image) {
        try {
            byte[] bytes = image.getBytes();
            String filename = image.getOriginalFilename() == null ? "image" : image.getOriginalFilename();
            return new ByteArrayResource(bytes) {
                @Override
                public String getFilename() {
                    return filename;
                }
            };
        } catch (IOException ex) {
            throw new AiNutritionBadRequestException("Unable to read the uploaded image");
        }
    }

    private boolean isTimeout(ResourceAccessException ex) {
        String message = ex.getMessage();
        if (message == null) {
            return false;
        }
        String lower = message.toLowerCase();
        return lower.contains("timed out") || lower.contains("read timed out") || lower.contains("connect timed out");
    }
}