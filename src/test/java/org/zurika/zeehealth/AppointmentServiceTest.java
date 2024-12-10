package org.zurika.zeehealth;

import org.junit.jupiter.api.*;
import org.mockito.*;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.zurika.zeehealth.model.*;
import org.zurika.zeehealth.repository.*;
import org.zurika.zeehealth.service.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllAppointments() {
        Page<Appointment> mockPage = new PageImpl<>(List.of(new Appointment(), new Appointment()));
        when(appointmentRepository.findAll(any(Pageable.class))).thenReturn(mockPage);

        List<Appointment> appointments = appointmentService.getAllAppointments(PageRequest.of(0, 10)).getContent();
        assertNotNull(appointments);
        assertEquals(2, appointments.size());

        verify(appointmentRepository, times(1)).findAll(any(Pageable.class));
    }


    @Test
    void testScheduleAppointment() {
        // Arrange: Mock data
        Long patientId = 1L;
        Long doctorId = 2L;
        LocalDateTime appointmentDate = LocalDateTime.of(2024, 12, 10, 10, 0);

        User patient = new User();
        patient.setId(patientId);
        patient.setUsername("patient");
        patient.setEmail("patient@example.com");

        User doctor = new User();
        doctor.setId(doctorId);
        doctor.setUsername("doctor");
        doctor.setEmail("doctor@example.com");

        Appointment mockAppointment = new Appointment();
        mockAppointment.setId(1L);
        mockAppointment.setPatient(patient);
        mockAppointment.setDoctor(doctor);
        mockAppointment.setAppointmentDate(appointmentDate);
        mockAppointment.setStatus(AppointmentStatus.CONFIRMED);

        when(userRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(userRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(mockAppointment);

        // Call the method
        Appointment appointment = appointmentService.scheduleAppointment(patientId, doctorId, appointmentDate);

        // Verify the result
        assertNotNull(appointment);
        assertEquals(patientId, appointment.getPatient().getId());
        assertEquals(doctorId, appointment.getDoctor().getId());
        assertEquals(appointmentDate, appointment.getAppointmentDate());
        assertEquals(AppointmentStatus.CONFIRMED, appointment.getStatus());

        // Verify interactions
        verify(userRepository, times(1)).findById(patientId);
        verify(userRepository, times(1)).findById(doctorId);
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    void testCancelAppointment() {
        Appointment existingAppointment = new Appointment();
        existingAppointment.setId(1L);
        existingAppointment.setStatus(AppointmentStatus.CONFIRMED);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(existingAppointment));

        appointmentService.cancelAppointment(1L);

        assertEquals(AppointmentStatus.CANCELED, existingAppointment.getStatus());
        verify(appointmentRepository, times(1)).save(existingAppointment);
    }

    @Test
    void testRescheduleAppointment() {
        Appointment existingAppointment = new Appointment();
        existingAppointment.setId(1L);
        existingAppointment.setAppointmentDate(LocalDateTime.now());
        existingAppointment.setStatus(AppointmentStatus.CONFIRMED);

        LocalDateTime newDate = LocalDateTime.now().plusDays(1);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(existingAppointment));

        appointmentService.rescheduleAppointment(1L, newDate);

        assertEquals(newDate, existingAppointment.getAppointmentDate());
        assertEquals(AppointmentStatus.RESCHEDULED, existingAppointment.getStatus());
        verify(appointmentRepository, times(1)).save(existingAppointment);
    }
}

