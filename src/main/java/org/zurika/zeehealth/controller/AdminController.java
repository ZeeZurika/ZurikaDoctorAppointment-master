package org.zurika.zeehealth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zurika.zeehealth.model.*;
import org.zurika.zeehealth.service.*;

import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private ReportService reportService;

    //Display the admin dashboard
    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size) {
        Page<Appointment> appointmentPage = appointmentService.getAllAppointments(PageRequest.of(page, size));
        model.addAttribute("appointments", appointmentPage.getContent());
        model.addAttribute("totalPages", appointmentPage.getTotalPages());
        model.addAttribute("pageNumber", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("title", "Admin Dashboard");

        // Get users sorted by id (newest first)
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("reportTypes", new String[]{"appointments-by-patient", "appointments-by-doctor"});
        return "admin-dashboard";
    }

    //Add a new user
    @PostMapping("/admin/addUser")
    public String addUser(@RequestParam String username,
                          @RequestParam String email,
                          @RequestParam String password,
                          @RequestParam String firstName,
                          @RequestParam String lastName,
                          @RequestParam String role) {
        userService.addUser(username, email, password, firstName, lastName, role);
        return "redirect:/admin/dashboard";
    }

    //Delete a user by their ID
    @PostMapping("/admin/deleteUser")
    public String deleteUser(@RequestParam Long userId) {
        userService.deleteUser(userId);
        return "redirect:/admin/dashboard";
    }

    //Generate a report based on the selected type
    @PostMapping("/admin/generateReport")
    public String generateReport(@RequestParam String reportType,
                                 @RequestParam(defaultValue = "manageUsers") String activeTab,
                                 Model model) {
        try {
            // Logic to generate the report
            String reportUrl = reportService.generateReport(reportType);
            model.addAttribute("successMessage",
                    "Report generated successfully! Download it <a href='" +
                            reportUrl + "'>here</a>.");
        } catch (Exception e) {
            model.addAttribute("errorMessage",
                    "Error generating report: " + e.getMessage());
        }
        // Add the active tab to the dashboard
        model.addAttribute("activeTab", activeTab);
        return "admin-dashboard";
    }

    // Generate report
    @PostMapping("/generateReport")
    public String generateReport(@RequestParam String reportType,
                                 RedirectAttributes redirectAttributes) {
        try {
            String fileUrl = reportService.generateReport(reportType); // Generate report and get file URL
            redirectAttributes.addFlashAttribute("successMessage",
                    "Report generated successfully! Download it <a href='" + fileUrl + "'>here</a>.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error generating report: " + e.getMessage());
        }
        redirectAttributes.addFlashAttribute("activeTab", "reports"); // Stay on Reports tab
        return "redirect:/admin/dashboard";
    }

    private String getString(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, Model model) {
        Page<Appointment> appointmentPage = appointmentService.getAllAppointments(PageRequest.of(page, size));
        model.addAttribute("appointments", appointmentPage.getContent());
        model.addAttribute("totalPages", appointmentPage.getTotalPages());
        model.addAttribute("pageNumber", page);
        model.addAttribute("pageSize", size);

        model.addAttribute("reportTypes", new String[]{"appointments-by-patient", "appointments-by-doctor"});
        model.addAttribute("users", userService.getAllUsers());

        return "admin-dashboard";
    }

}
