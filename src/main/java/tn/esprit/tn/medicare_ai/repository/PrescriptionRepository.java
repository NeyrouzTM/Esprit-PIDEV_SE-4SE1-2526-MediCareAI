package tn.esprit.tn.medicare_ai.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.tn.medicare_ai.entity.Prescription;

import java.util.List;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    List<Prescription> findByMedicalRecordId(Long medicalRecordId);

    Page<Prescription> findByPatientId(Long patientId, Pageable pageable);

    Page<Prescription> findByDoctorId(Long doctorId, Pageable pageable);

    Page<Prescription> findByPatientIdOrderByIssueDateDesc(Long patientId, Pageable pageable);

    Page<Prescription> findByDoctorIdOrderByIssueDateDesc(Long doctorId, Pageable pageable);
}
