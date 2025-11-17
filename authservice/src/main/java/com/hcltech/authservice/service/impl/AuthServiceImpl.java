package com.hcltech.authservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hcltech.authservice.dto.MessageResponseDTO;
import com.hcltech.authservice.dto.UserLoginRequestDTO;
import com.hcltech.authservice.dto.UserLoginResponseDTO;
import com.hcltech.authservice.dto.UserRegisterRequestDTO;
import com.hcltech.authservice.dto.UserRegisterResponseDTO;
import com.hcltech.authservice.entity.User;
import com.hcltech.authservice.entity.UserRole;
import com.hcltech.authservice.exception.UserNotFoundException;
import com.hcltech.authservice.repository.UserRepository;
import com.hcltech.authservice.security.MyUserDetailsService;
import com.hcltech.authservice.service.AuthService;
import com.hcltech.authservice.util.JwtUtil;
import com.hcltech.authservice.util.UserConverter;

import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of the AuthService interface providing authentication business logic.
 *
 * <p>This service handles user registration, login, logout, and user information retrieval
 * using Spring Security for authentication and JWT for token management.
 *
 * <p>Key responsibilities:
 * <ul>
 *   <li>User authentication with credential validation</li>
 *   <li>User registration with duplicate checking</li>
 *   <li>JWT token generation and management</li>
 *   <li>Password encoding and security</li>
 *   <li>Role assignment and management</li>
 * </ul>
 *
 * @author Boobesh Kumar S
 * @version 1.0
 * @since 2025-11-17
 * @see AuthService
 * @see AuthenticationManager
 * @see UserRepository
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private UserConverter userConverter;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Authenticates a user and returns login response with JWT token.
     *
     * <p>Process flow:
     * <ol>
     *   <li>Find user by email</li>
     *   <li>Authenticate credentials using Spring Security</li>
     *   <li>Set authentication in security context</li>
     *   <li>Generate JWT token with user details</li>
     *   <li>Build and return login response</li>
     * </ol>
     *
     * @param loginRequest the user credentials for authentication
     * @return UserLoginResponseDTO containing user details, JWT token, and expiration
     * @throws UserNotFoundException if no user found with the provided email
     * @throws org.springframework.security.authentication.BadCredentialsException if password is incorrect
     */
    @Override
    public UserLoginResponseDTO login(UserLoginRequestDTO loginRequest) {
        User userToLogin = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found", "Email", loginRequest.getEmail()));

        // ✅ Use raw password from request, not encoded one
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = userDetailsService.loadUserByUsername(userToLogin.getEmail());
        String token = jwtUtil.generateToken(userDetails.getUsername(), userToLogin.getEmail(), userToLogin.getRoles());
        long validityHours = jwtUtil.getTokenValidityInHours(token);

        UserLoginResponseDTO response = userConverter.loginEntityToResponse(userToLogin);
        response.setToken(token);
        response.setExpiry(validityHours);

        return response;
    }

    /**
     * Registers a new user with the system.
     *
     * <p>Registration process:
     * <ol>
     *   <li>Check if username already exists</li>
     *   <li>Check if email already exists</li>
     *   <li>Encode password for secure storage</li>
     *   <li>Assign appropriate roles (defaults to STUDENT)</li>
     *   <li>Save user to database</li>
     * </ol>
     *
     * @param registerRequest the user registration data
     * @return ResponseEntity with success message or error if registration fails
     */
    @Override
    public ResponseEntity<MessageResponseDTO> register(UserRegisterRequestDTO registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponseDTO("Error: Username already taken!"));
        }
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponseDTO("Error: Email already in use!"));
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(encoder.encode(registerRequest.getPassword()));

        Set<UserRole> roles = new HashSet<>();
        if (registerRequest.getRoles()==null) {
            roles.add(UserRole.STUDENT);
            user.setRoles(roles);
        } else {
            if (registerRequest.getRoles().contains(UserRole.STUDENT)) {
                roles.add(UserRole.STUDENT);
            }
            if (registerRequest.getRoles().contains(UserRole.ADMIN)) {
                roles.add(UserRole.ADMIN);
            }
            user.setRoles(roles);
        }

        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponseDTO("User registered successfully!"));
    }

    /**
     * Creates a response cookie to clear the JWT token during logout.
     *
     * <p>The cookie is configured with:
     * <ul>
     *   <li>Null value to clear the token</li>
     *   <li>Path set to root for application-wide coverage</li>
     *   <li>HttpOnly flag for security</li>
     *   <li>Max age of 0 to expire immediately</li>
     * </ul>
     *
     * @return ResponseCookie configured to clear the authentication cookie
     */
    @Override
    public ResponseCookie logoutUser() {
        return ResponseCookie.from("jwt", null).path("/").httpOnly(true).maxAge(0).build();
    }

    /**
     * Retrieves detailed information about the currently authenticated user.
     *
     * <p>User lookup strategy:
     * <ol>
     *   <li>First attempts to find user by email</li>
     *   <li>If not found, attempts to find by username</li>
     *   <li>Throws exception if user not found by either identifier</li>
     * </ol>
     *
     * @param authentication the Spring Security authentication object
     * @return UserRegisterResponseDTO with user details
     * @throws RuntimeException if user cannot be found in the database
     */
    @Override
    public UserRegisterResponseDTO getCurrentUserDetails(Authentication authentication) {
        if (authentication == null)
            return null;
        String identifier = authentication.getName();

        // ✅ Try email first, then username
        User user = userRepository.findByEmail(identifier)
                .or(() -> userRepository.findByUsername(identifier))
                .orElseThrow(() -> new RuntimeException("User not found: " + identifier));

        return userConverter.registerEntityToResponse(user);
    }
}