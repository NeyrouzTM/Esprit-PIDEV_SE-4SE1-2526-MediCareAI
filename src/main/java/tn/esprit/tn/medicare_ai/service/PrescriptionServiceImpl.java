package tn.esprit.tn.medicare_ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.tn.medicare_ai.dto.PrescriptionDTO;
import tn.esprit.tn.medicare_ai.dto.request.PrescriptionItemRequest;
import tn.esprit.tn.medicare_ai.dto.request.PrescriptionRequest;
import tn.esprit.tn.medicare_ai.dto.request.UploadPrescriptionRequest;
import tn.esprit.tn.medicare_ai.dto.response.PrescriptionDetailResponse;
import tn.esprit.tn.medicare_ai.dto.response.PrescriptionItemResponse;
import tn.esprit.tn.medicare_ai.dto.response.PrescriptionResponse;
import tn.esprit.tn.medicare_ai.dto.response.PrescriptionVerificationResponse;
import tn.esprit.tn.medicare_ai.entity.*;
import tn.esprit.tn.medicare_ai.exception.ResourceNotFoundException;
import tn.esprit.tn.medicare_ai.repository.MedicalRecordRepository;
import tn.esprit.tn.medicare_ai.repository.MedicineRepository;
import tn.esprit.tn.medicare_ai.repository.PrescriptionRepository;
import tn.esprit.tn.medicare_ai.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final UserRepository userRepository;
    private final MedicineRepository medicineRepository;

    @Override
    public Prescription create(PrescriptionDTO dto) {
        if (dto.getMedicalRecordId() == null) {
            throw new IllegalArgumentException("Medical record ID required");
        }
        if (dto.getDoctorId() == null) {
            throw new IllegalArgumentException("Doctor ID required");
        }
        if (dto.getMedicationName() == null || dto.getMedicationName().isBlank()) {
            throw new IllegalArgumentException("Medication name required");
        }

        MedicalRecord record = medicalRecordRepository.findById(dto.getMedicalRecordId())
                .orElseThrow(() -> new IllegalArgumentException("Medical record not found"));

        User doctor = userRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));

        Prescription prescription = Prescription.builder()
                .medicalRecord(record)
                .patient(record.getPatient())
                .doctor(doctor)
                .medicationName(dto.getMedicationName())
                .dosage(dto.getDosage())
                .duration(dto.getDuration())
                .instructions(dto.getInstructions())
                .prescriptionDate(dto.getPrescriptionDate())
                .issueDate(dto.getPrescriptionDate())
                .active(true)
                .status(PrescriptionStatus.ACTIVE)
                .build();

        return prescriptionRepository.save(prescription);
    }

    @Override
    @Transactional(readOnly = true)
    public Prescription getById(Long id) {
        return prescriptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Prescription not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Prescription> getByMedicalRecordId(Long medicalRecordId) {
        return prescriptionRepository.findByMedicalRecordId(medicalRecordId);
    }

    @Override
    public Prescription update(Long id, PrescriptionDTO dto) {
        Prescription prescription = getById(id);
        if (dto.getMedicationName() != null) {
            prescription.setMedicationName(dto.getMedicationName());
        }
        if (dto.getDosage() != null) {
            prescription.setDosage(dto.getDosage());
        }
        if (dto.getDuration() != null) {
            prescription.setDuration(dto.getDuration());
        }
        if (dto.getInstructions() != null) {
            prescription.setInstructions(dto.getInstructions());
        }
        if (dto.getPrescriptionDate() != null) {
            prescription.setPrescriptionDate(dto.getPrescriptionDate());
            prescription.setIssueDate(dto.getPrescriptionDate());
        }
        prescription.setActive(dto.isActive());
        prescription.setStatus(dto.isActive() ? PrescriptionStatus.ACTIVE : PrescriptionStatus.CANCELLED);
        return prescriptionRepository.save(prescription);
    }

    @Override
    public void delete(Long id) {
        Prescription prescription = getById(id);
        prescriptionRepository.delete(prescription);
    }

    @Override
    public PrescriptionDetailResponse createPrescription(PrescriptionRequest request, Long doctorId) {
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found: " + doctorId));
        if (doctor.getRole() != Role.DOCTOR) {
            throw new IllegalArgumentException("Only doctors can create prescriptions");
        }

        User patient = userRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + request.getPatientId()));

        MedicalRecord medicalRecord = medicalRecordRepository.findByPatient(patient)
                .orElseThrow(() -> new ResourceNotFoundException("Medical record not found for patient: " + request.getPatientId()));

        List<PrescriptionItem> items = request.getItems().stream()
                .map(itemRequest -> toEntityItem(itemRequest, null))
                .toList();

        if (items.isEmpty()) {
            throw new IllegalArgumentException("At least one prescription item is required");
        }

        PrescriptionItem firstItem = items.get(0);
        Prescription prescription = Prescription.builder()
                .medicalRecord(medicalRecord)
                .patient(patient)
                .doctor(doctor)
                .medicationName(firstItem.getMedicine() != null ? firstItem.getMedicine().getName() : "Medication")
                .dosage(firstItem.getDosage())
                .duration(firstItem.getDurationDays() != null ? firstItem.getDurationDays().toString() : null)
                .instructions(firstItem.getInstructions())
                .prescriptionDate(LocalDate.now())
                .active(true)
                .issueDate(LocalDate.now())
                .expiryDate(request.getExpiryDate())
                .status(PrescriptionStatus.ACTIVE)
                .build();

        for (PrescriptionItem item : items) {
            item.setPrescription(prescription);
        }
        prescription.setItems(items);

        Prescription saved = prescriptionRepository.save(prescription);
        return toDetail(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PrescriptionResponse> getPrescriptionsByPatient(Long patientId, Pageable pageable) {
        return prescriptionRepository.findByPatientIdOrderByIssueDateDesc(patientId, pageable)
                .map(this::toSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PrescriptionResponse> getPrescriptionsByDoctor(Long doctorId, Pageable pageable) {
        return prescriptionRepository.findByDoctorIdOrderByIssueDateDesc(doctorId, pageable)
                .map(this::toSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public PrescriptionDetailResponse getPrescriptionById(Long id) {
        return toDetail(getById(id));
    }

    @Override
    public PrescriptionVerificationResponse uploadPrescription(UploadPrescriptionRequest request, Long patientId) {
        userRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + patientId));

        if (request.getImageFile() == null || request.getImageFile().isEmpty()) {
            throw new IllegalArgumentException("Prescription image is required");
        }

        return PrescriptionVerificationResponse.builder()
                .id(System.currentTimeMillis())
                .status("PENDING_VERIFICATION")
                .message("Prescription uploaded successfully")
                .build();
    }

    private PrescriptionItem toEntityItem(PrescriptionItemRequest request, Prescription prescription) {
        Medicine medicine = medicineRepository.findById(request.getMedicineId())
                .orElseThrow(() -> new ResourceNotFoundException("Medicine not found: " + request.getMedicineId()));

        PrescriptionItem item = new PrescriptionItem();
        item.setPrescription(prescription);
        item.setMedicine(medicine);
        item.setQuantity(request.getQuantity());
        item.setDosage(request.getDosage());
        item.setFrequency(request.getFrequency());
        item.setDurationDays(request.getDurationDays());
        item.setInstructions(request.getInstructions());
        item.setRefills(request.getRefills() != null ? request.getRefills() : 0);
        return item;
    }

    private PrescriptionResponse toSummary(Prescription prescription) {
        User patient = resolvePatient(prescription);
        LocalDate issueDate = prescription.getIssueDate() != null ? prescription.getIssueDate() : prescription.getPrescriptionDate();
        Integer itemCount = prescription.getItems() != null && !prescription.getItems().isEmpty()
                ? prescription.getItems().size()
                : (prescription.getMedicationName() != null ? 1 : 0);

        PrescriptionStatus status = prescription.getStatus() != null
                ? prescription.getStatus()
                : (prescription.isActive() ? PrescriptionStatus.ACTIVE : PrescriptionStatus.CANCELLED);

        return PrescriptionResponse.builder()
                .id(prescription.getId())
                .patientId(patient != null ? patient.getId() : null)
                .patientName(patient != null ? patient.getFullName() : null)
                .doctorId(prescription.getDoctor() != null ? prescription.getDoctor().getId() : null)
                .doctorName(prescription.getDoctor() != null ? prescription.getDoctor().getFullName() : null)
                .issueDate(issueDate)
                .expiryDate(prescription.getExpiryDate())
                .status(status)
                .itemCount(itemCount)
                .build();
    }

    private PrescriptionDetailResponse toDetail(Prescription prescription) {
        PrescriptionResponse summary = toSummary(prescription);

        List<PrescriptionItemResponse> items = prescription.getItems() != null && !prescription.getItems().isEmpty()
                ? prescription.getItems().stream().map(this::toItemResponse).toList()
                : buildLegacyItemFallback(prescription);

        return PrescriptionDetailResponse.prescriptionDetailBuilder()
                .id(summary.getId())
                .patientId(summary.getPatientId())
                .patientName(summary.getPatientName())
                .doctorId(summary.getDoctorId())
                .doctorName(summary.getDoctorName())
                .issueDate(summary.getIssueDate())
                .expiryDate(summary.getExpiryDate())
                .status(summary.getStatus())
                .itemCount(summary.getItemCount())
                .items(items)
                .build();
    }

    private List<PrescriptionItemResponse> buildLegacyItemFallback(Prescription prescription) {
        if (prescription.getMedicationName() == null || prescription.getMedicationName().isBlank()) {
            return List.of();
        }
        return List.of(PrescriptionItemResponse.builder()
                .medicineName(prescription.getMedicationName())
                .dosage(prescription.getDosage())
                .durationDays(parseDurationDays(prescription.getDuration()))
                .instructions(prescription.getInstructions())
                .quantity(1)
                .refills(0)
                .build());
    }

    private Integer parseDurationDays(String duration) {
        if (duration == null || duration.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(duration.trim());
        } catch (NumberFormatException ignored) {
            return null;
        }
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

    private User resolvePatient(Prescription prescription) {
        if (prescription.getPatient() != null) {
            return prescription.getPatient();
        }
        return prescription.getMedicalRecord() != null ? prescription.getMedicalRecord().getPatient() : null;
    }
}