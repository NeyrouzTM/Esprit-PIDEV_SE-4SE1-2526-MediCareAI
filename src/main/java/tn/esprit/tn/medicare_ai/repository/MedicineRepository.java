package tn.esprit.tn.medicare_ai.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.tn.medicare_ai.entity.Medicine;
import tn.esprit.tn.medicare_ai.entity.MedicinieCategory;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    @Query("""
            select m from Medicine m
            where (:keyword is null or lower(m.name) like lower(concat('%', :keyword, '%'))
                or lower(m.genericName) like lower(concat('%', :keyword, '%'))
                or lower(m.manufacturer) like lower(concat('%', :keyword, '%')))
              and (:category is null or m.category = :category)
              and (:prescriptionRequired is null or m.prescriptionRequired = :prescriptionRequired)
            """)
    Page<Medicine> search(@Param("keyword") String keyword,
                          @Param("category") MedicinieCategory category,
                          @Param("prescriptionRequired") Boolean prescriptionRequired,
                          Pageable pageable);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
