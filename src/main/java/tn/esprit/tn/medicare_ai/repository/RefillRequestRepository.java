package tn.esprit.tn.medicare_ai.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.tn.medicare_ai.entity.RefillRequest;
import tn.esprit.tn.medicare_ai.entity.RefillStatus;

public interface RefillRequestRepository extends JpaRepository<RefillRequest, Long> {
    Page<RefillRequest> findByPatientIdOrderByRequestDateDesc(Long patientId, Pageable pageable);
    Page<RefillRequest> findByPatientIdAndStatus(Long patientId, RefillStatus status, Pageable pageable);
}

