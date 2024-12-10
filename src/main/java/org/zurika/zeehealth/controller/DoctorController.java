package org.zurika.zeehealth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zurika.zeehealth.model.*;
import org.zurika.zeehealth.service.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/doctor")
public class DoctorController {

    @Autowired
    private UserService userService;

    @Autowired
    private AppointmentService appointmentService;

    // Display the doctor dashboard
    @GetMapping("/dashboard")
    public String doctorDashboard(Model model,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size) {
        // Fetch the logged-in doctor
        User doctor = userService.getByUsername(userDetails.getUsername());

        // Fetch appointments assigned to the doctor
        Page<Appointment> appointmentsPage = appointmentService
                .getDoctorAppointments(doctor.getId(), PageRequest.of(page, size));

        model.addAttribute("appointments", appointmentsPage.getContent());
        model.addAttribute("totalPages", appointmentsPage.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("title", "Doctor Dashboard");

        return "doctor-dashboard";
    }

    // Update the status of an appointment (Confirm, Complete, Cancel)
    @PostMapping("/updateStatus")
    public String updateAppointmentStatus(@RequestParam Long appointmentId,
                                          @RequestParam String status,
                                          RedirectAttributes redirectAttributes) {
        try {
            // Update the status of the appointment
            appointmentService.updateAppointmentStatus(appointmentId, status);
            redirectAttributes.addFlashAttribute("successMessage", "Appointment status updated successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid status or appointment ID: " + e.getMessage());
        }
        return "redirect:/doctor/dashboard";
    }


    // Update an appointment, including rescheduling
    @PostMapping("/updateAppointment")
    public String updateAppointment(@RequestParam Long appointmentId,
                                    @RequestParam String status,
                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newDate,
                                    RedirectAttributes redirectAttributes) {
        try {
            // Convert string status to AppointmentStatus enum
            AppointmentStatus appointmentStatus = AppointmentStatus.valueOf(status.toUpperCase());

            // Update the appointment (status and optionally reschedule)
            appointmentService.updateAppointment(appointmentId, appointmentStatus, newDate);

            redirectAttributes.addFlashAttribute("successMessage", "Appointment updated successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid input: " + e.getMessage());
        }
        return "redirect:/doctor/dashboard";
    }
}
