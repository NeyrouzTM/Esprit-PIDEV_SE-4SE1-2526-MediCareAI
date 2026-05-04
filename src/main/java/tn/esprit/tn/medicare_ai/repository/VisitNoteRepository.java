package tn.esprit.tn.medicare_ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.tn.medicare_ai.entity.VisitNote;

import java.util.List;

public interface VisitNoteRepository
        extends JpaRepository<VisitNote, Long> {
    List<VisitNote> findByMedicalRecordId(Long medicalRecordId);

    List<VisitNote> findByDoctor_Id(Long doctorId);

    @Query("""
            select vn from VisitNote vn
            join vn.medicalRecord mr
            join mr.patient p
            join vn.doctor d
            where (:patientKeyword is null or lower(p.fullName) like lower(concat('%', :patientKeyword, '%')))
              and (:doctorKeyword is null or lower(d.fullName) like lower(concat('%', :doctorKeyword, '%')))
              and (
                  :clinicalKeyword is null
                  or lower(vn.subjective) like lower(concat('%', :clinicalKeyword, '%'))
                  or lower(vn.objective) like lower(concat('%', :clinicalKeyword, '%'))
                  or lower(vn.assessment) like lower(concat('%', :clinicalKeyword, '%'))
                  or lower(vn.plan) like lower(concat('%', :clinicalKeyword, '%'))
              )
            order by vn.visitDate desc
            """)
    List<VisitNote> searchClinicalNotes(
            @Param("patientKeyword") String patientKeyword,
            @Param("doctorKeyword") String doctorKeyword,
            @Param("clinicalKeyword") String clinicalKeyword
    );
}