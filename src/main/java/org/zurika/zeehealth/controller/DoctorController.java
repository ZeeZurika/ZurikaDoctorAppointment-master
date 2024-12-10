package org.zurika.zeehealth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zurika.zeehealth.model.Appointment;
import org.zurika.zeehealth.model.AppointmentStatus;
import org.zurika.zeehealth.model.User;
import org.zurika.zeehealth.service.*;

import java.time.LocalDateTime;

@Controller
public class DoctorController {

    @Autowired
    private UserService userService;

    @Autowired
    private AppointmentService appointmentService;

    // Display the doctor dashboard
    @GetMapping("/doctor/dashboard")
    public String doctorDashboard(Model model,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size) {
        User doctor = userService.getByUsername(userDetails.getUsername());
        Page<Appointment> appointmentsPage = appointmentService
                .getDoctorAppointments(doctor.getId(), PageRequest.of(page, size));

        model.addAttribute("appointments", appointmentsPage.getContent());
        model.addAttribute("totalPages", appointmentsPage.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("title", "Doctor Dashboard");

        return "doctor-dashboard";
    }


    // Update the status of an appointment (Confirm, Complete, Cancel, Reschedule)
    @PostMapping("/doctor/updateStatus")
    public String updateAppointmentStatus(@RequestParam Long appointmentId,
                                          @RequestParam String status,
                                          RedirectAttributes redirectAttributes) {
        try {
            AppointmentStatus appointmentStatus = AppointmentStatus.valueOf(status.toUpperCase());
            appointmentService.updateAppointmentStatus(appointmentId, appointmentStatus);
            redirectAttributes.addFlashAttribute("successMessage", "Appointment status updated successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid status or appointment ID: " + e.getMessage());
        }
        return "redirect:/doctor/dashboard";
    }
}

