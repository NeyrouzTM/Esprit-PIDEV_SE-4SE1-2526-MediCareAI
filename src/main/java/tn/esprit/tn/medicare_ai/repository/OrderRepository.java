package tn.esprit.tn.medicare_ai.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.tn.medicare_ai.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByPatientIdOrderByOrderDateDesc(Long patientId, Pageable pageable);
}

