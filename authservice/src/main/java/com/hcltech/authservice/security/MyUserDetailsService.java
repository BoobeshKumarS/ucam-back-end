package com.hcltech.authservice.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.hcltech.authservice.entity.User;
import com.hcltech.authservice.exception.UserNotFoundException;
import com.hcltech.authservice.repository.UserRepository;

import java.util.Collections;
import java.util.Optional;

/**
 * Custom implementation of Spring Security's UserDetailsService.
 *
 * <p>This service loads user-specific data during authentication and
 * converts application User entities into Spring Security UserDetails objects.
 *
 * <p>The service uses email as the username for authentication purposes
 * and provides user details including credentials and authorities.
 *
 * @author HCL Technologies
 * @version 1.0
 * @since 1.0
 * @see UserDetailsService
 * @see UserDetails
 */
@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Loads user by email address during authentication.
     *
     * <p>This method:
     * <ul>
     *   <li>Finds user by email in the database</li>
     *   <li>Creates Spring Security UserDetails object</li>
     *   <li>Assigns default "ROLE_USER" authority</li>
     *   <li>Throws exception if user not found</li>
     * </ul>
     *
     * @param email the email address identifying the user
     * @return UserDetails object containing user information for Spring Security
     * @throws UsernameNotFoundException if no user found with the provided email
     * @throws UserNotFoundException if user not found in the system
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return new org.springframework.security.core.userdetails.User(user.getUsername(),  user.getPassword(),
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
        }
        throw new UserNotFoundException("User not found", "email", email);
    }
}