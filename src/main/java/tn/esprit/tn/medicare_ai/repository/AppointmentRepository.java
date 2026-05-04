package tn.esprit.tn.medicare_ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.tn.medicare_ai.entity.Appointment;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository
        extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findByDoctorId(Long doctorId);
    List<Appointment> findByStatus(String status);

    List<Appointment> findByStatusAndAppointmentDateBefore(String status, LocalDateTime dateTime);

    @Query("""
            select a from Appointment a
            join a.patient p
            join a.doctor d
            where lower(d.fullName) like lower(concat('%', :doctorKeyword, '%'))
              and a.status in :statuses
              and a.appointmentDate between :start and :end
            order by a.appointmentDate asc
            """)
    List<Appointment> findUpcomingForDoctorKeyword(
            @Param("doctorKeyword") String doctorKeyword,
            @Param("statuses") List<String> statuses,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
            select a from Appointment a
            join a.patient p
            join a.doctor d
            where (:doctorId is null or d.id = :doctorId)
              and (:patientKeyword is null or lower(p.fullName) like lower(concat('%', :patientKeyword, '%')))
              and (:reasonKeyword is null or lower(a.reason) like lower(concat('%', :reasonKeyword, '%')))
            order by a.appointmentDate desc
            """)
    List<Appointment> searchByKeywords(
            @Param("doctorId") Long doctorId,
            @Param("patientKeyword") String patientKeyword,
            @Param("reasonKeyword") String reasonKeyword
    );
}