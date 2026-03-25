package tn.esprit.tn.medicare_ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.tn.medicare_ai.dto.request.PrescriptionItemRequest;
import tn.esprit.tn.medicare_ai.dto.request.PrescriptionRequest;
import tn.esprit.tn.medicare_ai.dto.request.UploadPrescriptionRequest;
import tn.esprit.tn.medicare_ai.dto.response.PrescriptionDetailResponse;
import tn.esprit.tn.medicare_ai.dto.response.PrescriptionItemResponse;
import tn.esprit.tn.medicare_ai.dto.response.PrescriptionResponse;
import tn.esprit.tn.medicare_ai.dto.response.PrescriptionVerificationResponse;
import tn.esprit.tn.medicare_ai.entity.Medicine;
import tn.esprit.tn.medicare_ai.entity.Prescription;
import tn.esprit.tn.medicare_ai.entity.PrescriptionItem;
import tn.esprit.tn.medicare_ai.entity.PrescriptionStatus;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.exception.InvalidPrescriptionException;
import tn.esprit.tn.medicare_ai.exception.ResourceNotFoundException;
import tn.esprit.tn.medicare_ai.repository.MedicineRepository;
import tn.esprit.tn.medicare_ai.repository.PrescriptionRepository;
import tn.esprit.tn.medicare_ai.repository.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final MedicineRepository medicineRepository;
    private final UserRepository userRepository;

    public PrescriptionDetailResponse createPrescription(PrescriptionRequest request, Long doctorId) {
        User patient = userRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + request.getPatientId()));

        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found: " + doctorId));

        if (request.getExpiryDate().isBefore(LocalDate.now())) {
            throw new InvalidPrescriptionException("Prescription expiry date cannot be in the past");
        }

        Prescription prescription = new Prescription();
        prescription.setPatient(patient);
        prescription.setDoctor(doctor);
        prescription.setIssueDate(LocalDate.now());
        prescription.setExpiryDate(request.getExpiryDate());
        prescription.setStatus(PrescriptionStatus.ACTIVE);

        List<PrescriptionItem> items = new ArrayList<>();
        for (PrescriptionItemRequest itemRequest : request.getItems()) {
            Medicine medicine = medicineRepository.findById(itemRequest.getMedicineId())
                    .orElseThrow(() -> new ResourceNotFoundException("Medicine not found: " + itemRequest.getMedicineId()));

            PrescriptionItem item = new PrescriptionItem();
            item.setPrescription(prescription);
            item.setMedicine(medicine);
            item.setQuantity(itemRequest.getQuantity());
            item.setDosage(itemRequest.getDosage());
            item.setFrequency(itemRequest.getFrequency());
            item.setDurationDays(itemRequest.getDurationDays());
            item.setInstructions(itemRequest.getInstructions());
            item.setRefills(itemRequest.getRefills() != null ? itemRequest.getRefills() : 0);
            items.add(item);
        }

        prescription.setItems(items);
        Prescription saved = prescriptionRepository.save(prescription);
        return toDetailResponse(saved);
    }

    @Transactional(readOnly = true)
    public PrescriptionDetailResponse getPrescriptionById(Long id) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found: " + id));
        return toDetailResponse(prescription);
    }

    @Transactional(readOnly = true)
    public Page<PrescriptionResponse> getPrescriptionsByPatient(Long patientId, Pageable pageable) {
        return prescriptionRepository.findByPatientId(patientId, pageable)
                .map(this::toSummaryResponse);
    }

    @Transactional(readOnly = true)
    public Page<PrescriptionResponse> getPrescriptionsByDoctor(Long doctorId, Pageable pageable) {
        return prescriptionRepository.findByDoctorId(doctorId, pageable)
                .map(this::toSummaryResponse);
    }

    @Transactional(readOnly = true)
    public PrescriptionVerificationResponse uploadPrescription(UploadPrescriptionRequest request, Long patientId) {
        userRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + patientId));

        if (request.getImageFile() == null || request.getImageFile().isEmpty()) {
            throw new InvalidPrescriptionException("Uploaded image file is empty");
        }

        return PrescriptionVerificationResponse.builder()
                .id(System.currentTimeMillis())
                .status("PENDING_VERIFICATION")
                .message("Prescription uploaded successfully and is pending verification")
                .build();
    }

    private PrescriptionResponse toSummaryResponse(Prescription prescription) {
        return PrescriptionResponse.builder()
                .id(prescription.getId())
                .patientId(prescription.getPatient() != null ? prescription.getPatient().getId() : null)
                .patientName(prescription.getPatient() != null ? prescription.getPatient().getFullName() : null)
                .doctorId(prescription.getDoctor() != null ? prescription.getDoctor().getId() : null)
                .doctorName(prescription.getDoctor() != null ? prescription.getDoctor().getFullName() : null)
                .issueDate(prescription.getIssueDate())
                .expiryDate(prescription.getExpiryDate())
                .status(prescription.getStatus())
                .itemCount(prescription.getItems() != null ? prescription.getItems().size() : 0)
                .build();
    }

    private PrescriptionDetailResponse toDetailResponse(Prescription prescription) {
        List<PrescriptionItemResponse> itemResponses = prescription.getItems() == null
                ? List.of()
                : prescription.getItems().stream().map(this::toItemResponse).toList();

        return PrescriptionDetailResponse.prescriptionDetailBuilder()
                .id(prescription.getId())
                .patientId(prescription.getPatient() != null ? prescription.getPatient().getId() : null)
                .patientName(prescription.getPatient() != null ? prescription.getPatient().getFullName() : null)
                .doctorId(prescription.getDoctor() != null ? prescription.getDoctor().getId() : null)
                .doctorName(prescription.getDoctor() != null ? prescription.getDoctor().getFullName() : null)
                .issueDate(prescription.getIssueDate())
                .expiryDate(prescription.getExpiryDate())
                .status(prescription.getStatus())
                .itemCount(itemResponses.size())
                .items(itemResponses)
                .build();
    }

    private PrescriptionItemResponse toItemResponse(PrescriptionItem item) {
        return PrescriptionItemResponse.builder()
                .id(item.getId())
                .medicineId(item.getMedicine() != null ? item.getMedicine().getId() : null)
                .medicineName(item.getMedicine() != null ? item.getMedicine().getName() : null)
                .medicineImageUrl(item.getMedicine() != null ? item.getMedicine().getImageUrl() : null)
                .quantity(item.getQuantity())
                .dosage(item.getDosage())
                .frequency(item.getFrequency())
                .durationDays(item.getDurationDays())
                .instructions(item.getInstructions())
                .refills(item.getRefills())
                .build();
    }
}
