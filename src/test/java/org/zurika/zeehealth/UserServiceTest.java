package org.zurika.zeehealth;

import org.junit.jupiter.api.*;
import org.mockito.*;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.zurika.zeehealth.model.*;
import org.zurika.zeehealth.repository.*;
import org.zurika.zeehealth.service.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddUser() {
        // Arrange: Create a user and mock dependencies
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password123"); // Raw password before encoding
        user.setRole(UserRole.PATIENT);

        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword123");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L); // Mock ID assignment during save
            return savedUser;
        });

        // Call the addUser method
        User createdUser = userService.addUser(
                user.getUsername(),
                user.getEmail(),
                "password123",
                "TestFirstName",
                "TestLastName",
                "PATIENT"
        );

        // Verify the results
        assertNotNull(createdUser);
        assertEquals("testuser", createdUser.getUsername());
        assertEquals("hashedPassword123", createdUser.getPassword()); // Verify hashed password
        assertEquals(UserRole.PATIENT, createdUser.getRole());

        // Verify interactions
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(any(User.class));
    }



    @Test
    void testUpdatePatientInfoWithPassword() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setFirstName("OldFirstName");
        existingUser.setLastName("OldLastName");
        existingUser.setEmail("old@example.com");
        existingUser.setPassword("hashedPassword123");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("newPassword")).thenReturn("hashedNewPassword");

        // Call the method to update user info, including a new password
        userService.updatePatientInfo(1L, "NewFirstName", "NewLastName", "new@example.com", "newPassword");

        verify(userRepository, times(1)).save(existingUser);

        assertEquals("NewFirstName", existingUser.getFirstName());
        assertEquals("NewLastName", existingUser.getLastName());
        assertEquals("new@example.com", existingUser.getEmail());
        assertEquals("hashedNewPassword", existingUser.getPassword());
    }

    @Test
    void testUpdatePatientInfoWithoutPassword() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setFirstName("OldFirstName");
        existingUser.setLastName("OldLastName");
        existingUser.setEmail("old@example.com");
        existingUser.setPassword("hashedPassword123");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        // Call the method to update user info without changing the password
        userService.updatePatientInfo(1L, "NewFirstName", "NewLastName", "new@example.com", null);

        verify(userRepository, times(1)).save(existingUser);

        assertEquals("NewFirstName", existingUser.getFirstName());
        assertEquals("NewLastName", existingUser.getLastName());
        assertEquals("new@example.com", existingUser.getEmail());
        assertEquals("hashedPassword123", existingUser.getPassword());
    }
}
