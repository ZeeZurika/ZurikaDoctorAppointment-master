package org.zurika.zeehealth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.zurika.zeehealth.model.*;
import org.zurika.zeehealth.service.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class PatientController {

    @Autowired
    private UserService userService;

    @Autowired
    private AppointmentService appointmentService;

    // Displaying patient dashboard: View appointments with optional filtering and pagination
    @GetMapping("/patient/dashboard")
    public String patientDashboard(Model model,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   @RequestParam(required = false) String status) {
        User patient = userService.getByUsername(userDetails.getUsername());
        Page<Appointment> appointmentsPage = (status != null && !status.isBlank())
                ? appointmentService.getPatientAppointmentsByStatus(patient.getId(), status, PageRequest.of(page, size))
                : appointmentService.getPatientAppointments(patient.getId(), PageRequest.of(page, size));

        model.addAttribute("appointments", appointmentsPage.getContent());
        model.addAttribute("totalPages", appointmentsPage.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("status", status);
        model.addAttribute("doctors", userService.getAllDoctors()); // Fetch doctors on demand
        model.addAttribute("patient", patient); // Include patient info for forms
        model.addAttribute("title", "Patient Dashboard");
        return "patient-dashboard";
    }

    // Schedule a new appointment.
    @PostMapping("/patient/schedule")
    public String scheduleAppointment(@RequestParam Long doctorId,
                                      @RequestParam String appointmentDate,
                                      @AuthenticationPrincipal UserDetails userDetails,
                                      Model model) {
        try {
            User patient = userService.getByUsername(userDetails.getUsername());
            LocalDateTime appointmentDateTime = LocalDateTime.parse(appointmentDate);
            appointmentService.scheduleAppointment(patient.getId(), doctorId, appointmentDateTime);
            model.addAttribute("successMessage", "Appointment scheduled successfully!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error scheduling appointment: " + e.getMessage());
        }
        return "redirect:/patient/dashboard";
    }

    // Cancel an appointment
    @PostMapping("/patient/cancel")
    public String cancelAppointment(@RequestParam Long appointmentId, Model model) {
        try {
            appointmentService.cancelAppointment(appointmentId);
            model.addAttribute("successMessage", "Appointment canceled successfully!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error canceling appointment: " + e.getMessage());
        }
        return "redirect:/patient/dashboard";
    }

    //Update personal information.
    @PostMapping("/patient/updateInfo")
    public String updatePatientInfo(@RequestParam String firstName,
                                    @RequestParam String lastName,
                                    @RequestParam String email,
                                    @RequestParam(required = false) String password,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    Model model) {
        try {
            User patient = userService.getByUsername(userDetails.getUsername());
            userService.updatePatientInfo(patient.getId(), firstName, lastName, email, password);
            model.addAttribute("successMessage", "Information updated successfully!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error updating information: " + e.getMessage());
        }
        return "redirect:/patient/dashboard";
    }
}

