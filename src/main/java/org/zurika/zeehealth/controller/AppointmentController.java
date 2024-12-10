package org.zurika.zeehealth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.zurika.zeehealth.model.Appointment;
import org.zurika.zeehealth.service.AppointmentService;
import org.zurika.zeehealth.validation.CreateValidationGroup;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @GetMapping("/appointments")
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

    @GetMapping("/appointments/status")
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


    @GetMapping("/appointments/date-range")
    public String viewAppointmentsByDateRange(@RequestParam String startDate,
                                              @RequestParam String endDate,
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

    @PostMapping("/appointments")
    public String createAppointment(
            @Validated(CreateValidationGroup.class) @RequestBody Appointment appointment) {
        appointmentService.scheduleAppointment(appointment);
        return "redirect:/appointments";
    }

}
