package com.hcltech.universityservice.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hcltech.universityservice.dto.AdminRequestDTO;
import com.hcltech.universityservice.dto.AdminResponseDTO;
import com.hcltech.universityservice.service.AdminService;

import jakarta.validation.Valid;

/**
 * REST controller for managing university administrators.
 * <p>
 * This controller provides endpoints for admin-related operations including:
 * <ul>
 *   <li>Admin registration</li>
 *   <li>Admin profile retrieval and management</li>
 *   <li>Admin listing and deletion</li>
 * </ul>
 * </p>
 * <p>
 * Most endpoints require ADMIN role authentication, except for the registration endpoint.
 * </p>
 *
 * @author HCL Tech
 * @version 1.0
 * @since 1.0
 * @see AdminService
 * @see AdminRequestDTO
 * @see AdminResponseDTO
 */
@RestController
@RequestMapping("api/admins")
public class AdminController {

	/**
	 * Service layer for admin business logic.
	 */
	@Autowired
	private AdminService adminService;

	/**
	 * Registers a new university administrator.
	 * <p>
	 * This endpoint creates a new admin account and registers the user
	 * in the authentication service with ADMIN role.
	 * </p>
	 *
	 * @param adminRequest the admin registration details
	 * @return ResponseEntity containing the created admin details
	 */
	@PostMapping("/register")
	public ResponseEntity<AdminResponseDTO> registerAdmin(@Valid @RequestBody AdminRequestDTO adminRequest) {
		AdminResponseDTO adminResponse = adminService.registerAdmin(adminRequest);
		return new ResponseEntity<AdminResponseDTO>(adminResponse, HttpStatus.CREATED);
	}

	/**
	 * Retrieves an admin by their unique identifier.
	 *
	 * @param id the unique identifier of the admin
	 * @return ResponseEntity containing the admin details
	 */
	@GetMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<AdminResponseDTO> getAdminById(@PathVariable UUID id) {
		AdminResponseDTO adminResponse = adminService.getAdminById(id);
		return new ResponseEntity<AdminResponseDTO>(adminResponse, HttpStatus.OK);
	}

	/**
	 * Retrieves all registered administrators.
	 *
	 * @return ResponseEntity containing a list of all admin details
	 */
	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<AdminResponseDTO>> getAllAdmins() {
		List<AdminResponseDTO> admins = adminService.getAllAdmins();
		return new ResponseEntity<List<AdminResponseDTO>>(admins, HttpStatus.OK);
	}

	/**
	 * Updates an existing admin's information.
	 *
	 * @param id the unique identifier of the admin to update
	 * @param adminRequest the updated admin details
	 * @return ResponseEntity containing the updated admin details
	 */
	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<AdminResponseDTO> updateAdmin(@PathVariable UUID id,
			@Valid @RequestBody AdminRequestDTO adminRequest) {
		AdminResponseDTO adminResponse = adminService.updateAdmin(id, adminRequest);
		return new ResponseEntity<AdminResponseDTO>(adminResponse, HttpStatus.OK);
	}
	
	/**
	 * Deletes an admin account.
	 *
	 * @param id the unique identifier of the admin to delete
	 * @return ResponseEntity with no content status
	 */
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> deleteAdmin(@PathVariable UUID id) {
		adminService.deleteAdmin(id);
		return ResponseEntity.noContent().build();
	}
	
	/**
	 * Retrieves the currently authenticated admin's profile.
	 *
	 * @param authentication the authentication context containing the current user's credentials
	 * @return ResponseEntity containing the current admin's details
	 */
	@GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<AdminResponseDTO> getCurrentStudent(Authentication authentication) {
        return ResponseEntity.ok(adminService.getCurrentAdmin(authentication));
    }
}
