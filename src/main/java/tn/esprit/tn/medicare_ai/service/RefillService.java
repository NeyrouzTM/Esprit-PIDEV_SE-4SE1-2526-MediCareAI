package tn.esprit.tn.medicare_ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.tn.medicare_ai.dto.request.RefillRequestDto;
import tn.esprit.tn.medicare_ai.dto.response.RefillResponse;
import tn.esprit.tn.medicare_ai.entity.Prescription;
import tn.esprit.tn.medicare_ai.entity.RefillRequest;
import tn.esprit.tn.medicare_ai.entity.RefillStatus;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.exception.InvalidPrescriptionException;
import tn.esprit.tn.medicare_ai.exception.PrescriptionExpiredException;
import tn.esprit.tn.medicare_ai.exception.ResourceNotFoundException;
import tn.esprit.tn.medicare_ai.exception.UnauthorizedActionException;
import tn.esprit.tn.medicare_ai.repository.PrescriptionRepository;
import tn.esprit.tn.medicare_ai.repository.RefillRequestRepository;
import tn.esprit.tn.medicare_ai.repository.UserRepository;

import java.time.LocalDate;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class RefillService {

    private final RefillRequestRepository refillRequestRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final UserRepository userRepository;

    public RefillResponse requestRefill(RefillRequestDto request, Long patientId) {
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + patientId));

        Prescription prescription = prescriptionRepository.findById(request.getPrescriptionId())
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found: " + request.getPrescriptionId()));

        if (prescription.getPatient() == null || !prescription.getPatient().getId().equals(patientId)) {
            throw new UnauthorizedActionException("Prescription does not belong to current patient");
        }

        if (prescription.getExpiryDate() != null && prescription.getExpiryDate().isBefore(LocalDate.now())) {
            throw new PrescriptionExpiredException("Cannot request refill for expired prescription");
        }

        if (prescription.getStatus() == null || prescription.getStatus().name().equals("CANCELLED")) {
            throw new InvalidPrescriptionException("Prescription is not valid for refill request");
        }

        RefillRequest refillRequest = new RefillRequest();
        refillRequest.setPrescription(prescription);
        refillRequest.setPatient(patient);
        refillRequest.setRequestDate(LocalDate.now());
        refillRequest.setRequestedQuantity(request.getRequestedQuantity());
        refillRequest.setStatus(RefillStatus.PENDING);

        return toResponse(refillRequestRepository.save(refillRequest), "Refill request submitted");
    }

    public RefillResponse processRefill(Long refillId) {
        RefillRequest refillRequest = refillRequestRepository.findById(refillId)
                .orElseThrow(() -> new ResourceNotFoundException("Refill request not found: " + refillId));

        refillRequest.setStatus(RefillStatus.APPROVED);
        return toResponse(refillRequestRepository.save(refillRequest), "Refill request approved");
    }

    @Transactional(readOnly = true)
    public Page<RefillResponse> getRefillsByPatient(Long patientId, Pageable pageable) {
        return refillRequestRepository.findByPatientIdOrderByRequestDateDesc(patientId, pageable)
                .map(r -> toResponse(r, null));
    }

    public void deleteRefill(Long refillId, Long patientId) {
        RefillRequest refillRequest = refillRequestRepository.findById(refillId)
                .orElseThrow(() -> new ResourceNotFoundException("Refill request not found: " + refillId));

        if (refillRequest.getPatient() == null || !refillRequest.getPatient().getId().equals(patientId)) {
            throw new UnauthorizedActionException("Refill request does not belong to current patient");
        }

        if (refillRequest.getStatus() != RefillStatus.PENDING) {
            throw new UnauthorizedActionException("Only pending refill requests can be deleted");
        }

        refillRequestRepository.delete(refillRequest);
    }

    private RefillResponse toResponse(RefillRequest refillRequest, String message) {
        return RefillResponse.builder()
                .id(refillRequest.getId())
                .prescriptionId(refillRequest.getPrescription() != null ? refillRequest.getPrescription().getId() : null)
                .requestDate(refillRequest.getRequestDate())
                .status(refillRequest.getStatus())
                .message(message)
                .build();
    }
}

