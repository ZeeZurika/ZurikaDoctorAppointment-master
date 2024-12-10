package org.zurika.zeehealth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.zurika.zeehealth.model.Appointment;
import org.zurika.zeehealth.service.AppointmentService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    // View all appointments with pagination
    @GetMapping
    public String viewAllAppointments(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size,
                                      Model model) {
        Page<Appointment> appointmentPage = appointmentService.getAllAppointments(PageRequest.of(page, size));
        model.addAttribute("appointments", appointmentPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", appointmentPage.getTotalPages());
        model.addAttribute("filter", "all"); // Indicate the current view
        return "admin-dashboard";
    }

    // View appointments filtered by status
    @GetMapping("/status")
    public String viewAppointmentsByStatus(@RequestParam String status, Model model) {
        try {
            List<Appointment> appointments = appointmentService.getAppointmentsByStatus(status);
            model.addAttribute("appointments", appointments);
            model.addAttribute("filter", "status");
            model.addAttribute("status", status);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", "Invalid status: " + status);
        }
        return "admin-dashboard";
    }

    // View appointments within a date range
    @GetMapping("/date-range")
    public String viewAppointmentsByDateRange(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String startDate,
                                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String endDate,
                                              Model model) {
        try {
            LocalDateTime start = LocalDate.parse(startDate).atStartOfDay();
            LocalDateTime end = LocalDate.parse(endDate).atStartOfDay();

            if (end.isBefore(start)) {
                throw new IllegalArgumentException("End date cannot be before start date.");
            }

            List<Appointment> appointments = appointmentService.getAppointmentsByDateRange(start, end);
            model.addAttribute("appointments", appointments);
            model.addAttribute("filter", "date-range");
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Invalid date range: " + e.getMessage());
        }
        return "admin-dashboard";
    }

    // Create a new appointment
    @PostMapping
    public String createAppointment(@Validated @RequestBody Appointment appointment) {
        try {
            appointmentService.scheduleAppointment(appointment.getPatient().getId(),
                    appointment.getDoctor().getId(),
                    appointment.getAppointmentDate());
        } catch (Exception e) {
            throw new IllegalArgumentException("Error creating appointment: " + e.getMessage());
        }
        return "redirect:/appointments";
    }
}
