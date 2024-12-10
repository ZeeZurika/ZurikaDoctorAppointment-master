package org.zurika.zeehealth.repository;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zurika.zeehealth.model.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * The AppointmentRepository interface provides methods for interacting with the database
 * to perform CRUD operations on Appointment entities. It extends JpaRepository to leverage
 * built-in methods and supports custom query methods for specific requirements.
 */

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByStatus(AppointmentStatus status);
    List<Appointment> findByAppointmentDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    Page<Appointment> findByDoctorId(Long doctorId, Pageable pageable);
    Page<Appointment> findByPatientIdAndStatus(Long patientId, AppointmentStatus status, Pageable pageable);
    Page<Appointment> findByPatientId(Long patientId, Pageable pageable);

}

