package org.zurika.zeehealth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.zurika.zeehealth.model.Appointment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    private AppointmentService appointmentService;

    /**
     * Generate a report based on the specified report type.
     *
     * @param reportType The type of report to generate ("appointments-by-patient" or "appointments-by-doctor").
     * @return The path to the generated report file.
     * @throws IOException If there is an error during file writing.
     */
    public String generateReport(String reportType) throws IOException {
        StringBuilder reportContent = new StringBuilder();

        switch (reportType.toLowerCase()) {
            case "appointments-by-patient":
                generatePatientReport(reportContent);
                break;

            case "appointments-by-doctor":
                generateDoctorReport(reportContent);
                break;

            default:
                throw new IllegalArgumentException("Invalid report type: " + reportType);
        }

        // Save the report to a file
        String fileName = "report_" + reportType + "_" + System.currentTimeMillis() + ".txt";
        Path filePath = Paths.get("reports", fileName);
        Files.createDirectories(filePath.getParent()); // Ensure the directory exists
        Files.writeString(filePath, reportContent.toString());

        // Return the relative path for download
        return "/files/reports/" + fileName; // Adjust based on your file-serving endpoint configuration
    }

    /**
     * Generate content for the patient report.
     *
     * @param reportContent The StringBuilder to append the report data.
     */
    private void generatePatientReport(StringBuilder reportContent) {
        reportContent.append("Appointments by Patient:\n");
        // Fetch all appointments as a list
        List<Appointment> appointments = appointmentService.getAllAppointments(PageRequest.of(0, Integer.MAX_VALUE)).getContent();

        appointments.forEach(appointment -> {
            reportContent.append("Patient ID: ").append(appointment.getPatient().getId())
                    .append(", Patient Name: ").append(appointment.getPatient().getFirstName())
                    .append(" ").append(appointment.getPatient().getLastName())
                    .append(", Appointment Date: ").append(appointment.getAppointmentDate())
                    .append(", Status: ").append(appointment.getStatus()).append("\n");
        });
    }

    /**
     * Generate content for the doctor report.
     *
     * @param reportContent The StringBuilder to append the report data.
     */
    private void generateDoctorReport(StringBuilder reportContent) {
        reportContent.append("Appointments by Doctor:\n");
        // Fetch all appointments as a list
        List<Appointment> appointments = appointmentService.getAllAppointments(PageRequest.of(0, Integer.MAX_VALUE)).getContent();

        appointments.forEach(appointment -> {
            reportContent.append("Doctor ID: ").append(appointment.getDoctor().getId())
                    .append(", Doctor Name: ").append(appointment.getDoctor().getFirstName())
                    .append(" ").append(appointment.getDoctor().getLastName())
                    .append(", Appointment Date: ").append(appointment.getAppointmentDate())
                    .append(", Status: ").append(appointment.getStatus()).append("\n");
        });
    }
}


