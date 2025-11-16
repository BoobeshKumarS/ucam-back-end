package com.hcltech.universityservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.hcltech.universityservice.dto.PagedResponse;
import com.hcltech.universityservice.dto.UniversityRequestDTO;
import com.hcltech.universityservice.dto.UniversityResponseDTO;
import com.hcltech.universityservice.service.UniversityService;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing universities.
 * <p>
 * This controller provides comprehensive CRUD operations for universities including:
 * <ul>
 *   <li>Creating new universities (requires ADMIN role)</li>
 *   <li>Retrieving university information with pagination support</li>
 *   <li>Updating university details (requires ADMIN role)</li>
 *   <li>Deleting universities (requires ADMIN role)</li>
 *   <li>Querying universities by admin ID</li>
 * </ul>
 * </p>
 * <p>
 * Read operations are publicly accessible, while write operations require
 * authentication and ADMIN role authorization.
 * </p>
 *
 * @author HCL Tech
 * @version 1.0
 * @since 1.0
 * @see UniversityService
 * @see UniversityRequestDTO
 * @see UniversityResponseDTO
 */
@RestController
@RequestMapping("/api/universities")
@Tag(name = "University Management", description = "APIs for managing universities and course mappings")
@RequiredArgsConstructor
public class UniversityController {

	/**
	 * Service layer for university business logic.
	 */
	private final UniversityService universityService;

	@Operation(summary = "Create a new university")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "University successfully created"),
			@ApiResponse(responseCode = "400", description = "Invalid request payload") })
	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public UniversityResponseDTO create(@RequestBody UniversityRequestDTO universityRequestDTO,
			Authentication authentication) {

		return universityService.create(universityRequestDTO, authentication);
	}

	@Operation(summary = "Get all universities", description = "Retrieve a paginated list of all universities in the system")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Universities retrieved successfully"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	@GetMapping
	public PagedResponse<UniversityResponseDTO> getAllUniversities(@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int limit) {
		Pageable pageable = PageRequest.of(page - 1, limit);
		Page<UniversityResponseDTO> universityPage = universityService.getAll(pageable);

		List<UniversityResponseDTO> dtos = universityPage.getContent();

		return new PagedResponse<>(true, universityPage.getTotalElements(),
				new PagedResponse.Pagination(page, limit, universityPage.getTotalPages()), dtos);
	}

	@Operation(summary = "Get a university by ID")
	@GetMapping("/{id}")
	public UniversityResponseDTO getById(@PathVariable UUID id) {
		return universityService.getById(id);
	}
	
	@Operation(summary = "Get a university by Admin ID")
	@GetMapping("/admin/{adminId}")
	public UniversityResponseDTO getByAdminId(@PathVariable UUID adminId) {
		return universityService.getByAdminId(adminId);
	}

	@Operation(summary = "Delete university", description = "Permanently delete a university and its associated data")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "University deleted successfully"),
			@ApiResponse(responseCode = "401", description = "Unauthorized access"),
			@ApiResponse(responseCode = "403", description = "Insufficient permissions"),
			@ApiResponse(responseCode = "404", description = "University not found"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public void delete(@PathVariable UUID id) {
		universityService.delete(id);
	}

	@Operation(summary = "Update university", description = "Update university information. Requires UPDATE_UNIVERSITY_DETAILS permission for sub-admins")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "University updated successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid input data"),
			@ApiResponse(responseCode = "401", description = "Unauthorized access"),
			@ApiResponse(responseCode = "403", description = "Insufficient permissions"),
			@ApiResponse(responseCode = "404", description = "University not found"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public UniversityResponseDTO update(@PathVariable UUID id, @RequestBody UniversityRequestDTO dto) {
		return universityService.update(id, dto);
	}
}