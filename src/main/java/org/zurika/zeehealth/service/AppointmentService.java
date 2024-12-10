package org.zurika.zeehealth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.zurika.zeehealth.model.*;
import org.zurika.zeehealth.repository.*;

import java.time.*;
import java.util.*;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    // Get paginated appointments for a patient
    public Page<Appointment> getPatientAppointments(Long patientId, PageRequest pageRequest) {
        return appointmentRepository.findByPatientId(patientId, pageRequest);
    }

    // Get all appointments for a doctor
    public Page<Appointment> getDoctorAppointments(Long doctorId, PageRequest pageRequest) {
        return appointmentRepository.findByDoctorId(doctorId, pageRequest);
    }

    // Get paginated appointments for a patient filtered by status
    public Page<Appointment> getPatientAppointmentsByStatus(Long patientId, String status, PageRequest pageRequest) {
        try {
            AppointmentStatus appointmentStatus = AppointmentStatus.valueOf(status.toUpperCase());
            return appointmentRepository.findByPatientIdAndStatus(patientId, appointmentStatus, pageRequest);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
    }

    // Schedule a new appointment
    public Appointment scheduleAppointment(Long patientId, Long doctorId, LocalDateTime appointmentDate) {
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found!"));
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found!"));

        if (appointmentDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Appointment date must be in the future.");
        }

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(appointmentDate);
        appointment.setStatus(AppointmentStatus.PENDING);

        return appointmentRepository.save(appointment);
    }

    // Cancel an appointment
    public void cancelAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found!"));
        appointment.setStatus(AppointmentStatus.CANCELED);
        appointmentRepository.save(appointment);
    }

    // Update the status of an appointment
    public void updateAppointmentStatus(Long appointmentId, AppointmentStatus status) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found!"));
        appointment.setStatus(status);
        appointmentRepository.save(appointment); // Persist changes
    }

    // Reschedule an appointment
    public void rescheduleAppointment(Long appointmentId, LocalDateTime newDate) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found!"));
        appointment.setAppointmentDate(newDate);
        appointment.setStatus(AppointmentStatus.RESCHEDULED);
        appointmentRepository.save(appointment);
    }

    // Get appointments within a specific date range
    public List<Appointment> getAppointmentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return appointmentRepository.findByAppointmentDateBetween(startDate, endDate);
    }

    // Get all appointments by status
    public List<Appointment> getAppointmentsByStatus(String status) {
        AppointmentStatus appointmentStatus = AppointmentStatus.valueOf(status.toUpperCase());
        return appointmentRepository.findByStatus(AppointmentStatus.valueOf(String.valueOf(appointmentStatus)));
    }

    // Get all appointments with page
    public Page<Appointment> getAllAppointments(Pageable pageable) {
        return appointmentRepository.findAll(pageable);
    }

    public void scheduleAppointment(Appointment appointment) {
        User doctor = userRepository.findById(appointment.getDoctor().getId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found!"));
        User patient = userRepository.findById(appointment.getPatient().getId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found!"));

        // Validate appointment date
        if (appointment.getAppointmentDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Appointment date must be in the future.");
        }

        // Set additional attributes if necessary
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setStatus(AppointmentStatus.PENDING); // Default status

        // Save the appointment
        appointmentRepository.save(appointment);
    }
}


