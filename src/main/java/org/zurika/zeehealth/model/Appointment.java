package org.zurika.zeehealth.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.zurika.zeehealth.validation.CreateValidationGroup;

import java.time.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;

    @Column(nullable = false)
    @Future(message = "Appointment date must be in the future.", groups = CreateValidationGroup.class)
    private LocalDateTime appointmentDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;
}

