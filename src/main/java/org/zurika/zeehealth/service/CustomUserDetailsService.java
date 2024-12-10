package org.zurika.zeehealth.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.zurika.zeehealth.model.User;
import org.zurika.zeehealth.repository.*;
import java.util.*;

/**
 * CustomUserDetailsService is a service class that implements Spring Security's
 * {@link UserDetailsService} interface to provide custom user authentication logic.
 */

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    // Loads the user by username and converts the user information into Spring Security's format
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());

        // Return a Spring Security user object with the user's details and authorities
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(authority)
        );
    }

}

