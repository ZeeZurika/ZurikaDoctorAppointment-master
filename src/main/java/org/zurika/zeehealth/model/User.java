package org.zurika.zeehealth.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters.")
    private String username;

    @Column(nullable = false, unique = true)
    @Email(message = "Invalid email format.")
    private String email;

    @Column(nullable = false)
    @Size(min = 6, message = "Password must be at least 6 characters.")
    private String password;

    @Column(nullable = false)
    @NotEmpty(message = "First name is required.")
    private String firstName;

    @Column(nullable = false)
    @NotEmpty(message = "Last name is required.")
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appointment> patientAppointments;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appointment> doctorAppointments;

}
