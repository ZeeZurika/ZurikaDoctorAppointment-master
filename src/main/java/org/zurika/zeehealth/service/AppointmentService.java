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

    // Get all appointments with pagination
    public Page<Appointment> getAllAppointments(Pageable pageable) {
        return appointmentRepository.findAll(pageable);
    }

    // Get appointments by status
    public List<Appointment> getAppointmentsByStatus(String status) {
        AppointmentStatus appointmentStatus = AppointmentStatus.valueOf(status.toUpperCase());
        return appointmentRepository.findByStatus(appointmentStatus);
    }

    // Get appointments by date range
    public List<Appointment> getAppointmentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return appointmentRepository.findByAppointmentDateBetween(startDate, endDate);
    }

    // Get appointments for a specific patient
    public Page<Appointment> getPatientAppointments(Long patientId, PageRequest pageRequest) {
        return appointmentRepository.findByPatientId(patientId, pageRequest);
    }

    // Get patient appointments filtered by status
    public Page<Appointment> getPatientAppointmentsByStatus(Long patientId, String status, PageRequest pageRequest) {
        AppointmentStatus appointmentStatus = AppointmentStatus.valueOf(status.toUpperCase());
        return appointmentRepository.findByPatientIdAndStatus(patientId, appointmentStatus, pageRequest);
    }

    // Get doctor appointments
    public Page<Appointment> getDoctorAppointments(Long doctorId, PageRequest pageRequest) {
        return appointmentRepository.findByDoctorId(doctorId, pageRequest);
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

    // Update appointment (for status and rescheduling)
    public void updateAppointment(Long appointmentId, AppointmentStatus status, LocalDateTime newDate) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found!"));

        appointment.setStatus(status);
        if (status == AppointmentStatus.RESCHEDULED && newDate != null) {
            if (newDate.isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("Rescheduled date must be in the future.");
            }
            appointment.setAppointmentDate(newDate);
        }

        appointmentRepository.save(appointment);
    }

    public void updateAppointmentStatus(Long appointmentId, String status) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found!"));

        // Convert the string status to the AppointmentStatus enum
        try {
            AppointmentStatus appointmentStatus = AppointmentStatus.valueOf(status.toUpperCase());
            appointment.setStatus(appointmentStatus);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value: " + status);
        }

        // Save the updated appointment
        appointmentRepository.save(appointment);
    }

    // Cancel an appointment
    public void cancelAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found!"));
        appointment.setStatus(AppointmentStatus.CANCELED);
        appointmentRepository.save(appointment);
    }
}




