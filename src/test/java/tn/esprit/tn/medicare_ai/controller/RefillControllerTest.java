package tn.esprit.tn.medicare_ai.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import tn.esprit.tn.medicare_ai.dto.request.RefillRequestDto;
import tn.esprit.tn.medicare_ai.dto.request.UploadPrescriptionRequest;
import tn.esprit.tn.medicare_ai.dto.response.PrescriptionVerificationResponse;
import tn.esprit.tn.medicare_ai.dto.response.RefillResponse;
import tn.esprit.tn.medicare_ai.entity.RefillStatus;
import tn.esprit.tn.medicare_ai.entity.Role;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.service.PrescriptionService;
import tn.esprit.tn.medicare_ai.service.RefillService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefillControllerTest {

    @Mock
    private RefillService refillService;
    @Mock
    private PrescriptionService prescriptionService;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PharmacyController controller;

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("requestRefill: valid request returns refill details")
    void requestRefill_valid_returnsOk() {
        mockCurrentPatient();
        when(refillService.requestRefill(any(RefillRequestDto.class), org.mockito.ArgumentMatchers.eq(1L)))
                .thenReturn(RefillResponse.builder().id(9L).status(RefillStatus.PENDING).build());

        ResponseEntity<RefillResponse> response = controller.requestRefill(new RefillRequestDto());

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("PENDING", response.getBody().getStatus().name());
    }

    @Test
    @DisplayName("uploadPrescription: uploads mock file and returns verification response")
    void uploadPrescription_mockFile_returnsVerification() {
        mockCurrentPatient();
        UploadPrescriptionRequest request = UploadPrescriptionRequest.builder()
                .imageFile(new MockMultipartFile("imageFile", "rx.jpg", "image/jpeg", "img".getBytes()))
                .doctorName("Dr. Smith")
                .build();

        when(prescriptionService.uploadPrescription(any(UploadPrescriptionRequest.class), org.mockito.ArgumentMatchers.eq(1L)))
                .thenReturn(PrescriptionVerificationResponse.builder().id(123L).status("PENDING_VERIFICATION").build());

        ResponseEntity<PrescriptionVerificationResponse> response = controller.uploadPrescription(request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("PENDING_VERIFICATION", response.getBody().getStatus());
    }

    private void mockCurrentPatient() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("patient@med.com", null));
        User patient = new User();
        patient.setId(1L);
        patient.setEmail("patient@med.com");
        patient.setRole(Role.PATIENT);
        when(userRepository.findByEmail("patient@med.com")).thenReturn(Optional.of(patient));
    }
}
