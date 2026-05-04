package tn.esprit.tn.medicare_ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentSchedulerService {

    private final AppointmentService appointmentService;

    @Value("${app.appointments.reminder.window-minutes:30}")
    private int reminderWindowMinutes;

    @Scheduled(cron = "${app.appointments.reminder.cron:0 */10 * * * *}")
    public void processAppointments() {
        int expiredCount = appointmentService.expirePastPendingAppointments();
        int upcomingCount = appointmentService.findUpcomingForDoctorKeyword("", reminderWindowMinutes).size();

        log.info("Appointment scheduler executed: expired={}, upcomingInWindow={}", expiredCount, upcomingCount);
    }
}

