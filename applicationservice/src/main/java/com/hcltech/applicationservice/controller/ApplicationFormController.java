package com.hcltech.applicationservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.hcltech.applicationservice.dto.ApplicationFormRequestDTO;
import com.hcltech.applicationservice.dto.ApplicationFormResponseDTO;
import com.hcltech.applicationservice.dto.ApplicationValidationResponseDTO;
import com.hcltech.applicationservice.entity.ApplicationStatus;
import com.hcltech.applicationservice.service.ApplicationFormService;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing university application forms.
 *
 * This controller provides comprehensive endpoints for the complete lifecycle
 * of student applications to universities. It handles application creation,
 * retrieval, updates, deletion, submission, and validation operations.
 *
 * All endpoints require authentication via JWT token and appropriate role authorization
 * (STUDENT or ADMIN). The controller supports operations such as:
 * - Creating draft applications
 * - Submitting applications for review
 * - Retrieving applications by various criteria (ID, student, university, status)
 * - Updating application details and status
 * - Validating application ownership
 * - Paginated retrieval for large datasets
 *
 * Base path: /api/applications
 *
 * Security: All endpoints require STUDENT or ADMIN role via @PreAuthorize
 *
 * @author HCLTech
 * @version 1.0
 * @since 2025-01-01
 * @see ApplicationFormService
 * @see ApplicationFormRequestDTO
 * @see ApplicationFormResponseDTO
 */
@RestController
@RequestMapping("/api/applications")
@Tag(name = "Application Form API", description = "Endpoints for applying to university")
@RequiredArgsConstructor
public class ApplicationFormController {

	/**
	 * Service layer dependency for application form business logic.
	 */
    public final ApplicationFormService applicationFormService;

    @Operation(summary = "Create a new application")
    @ApiResponse(responseCode = "201", description = "Application created successfully")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @PostMapping("/{studentId}")
    public ResponseEntity<ApplicationFormResponseDTO> createApplication(
            @RequestBody @Valid ApplicationFormRequestDTO dto, @PathVariable UUID studentId) {
        ApplicationFormResponseDTO response = applicationFormService.createApplication(dto, studentId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get an application by ID")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ApplicationFormResponseDTO> getApplication(@PathVariable UUID id) {
        return ResponseEntity.ok(applicationFormService.getApplicationById(id));
    }

    @Operation(summary = "Get all applications")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @GetMapping
    public ResponseEntity<List<ApplicationFormResponseDTO>> getAllApplications() {
        List<ApplicationFormResponseDTO> applications = applicationFormService.getAllApplications();
        return ResponseEntity.ok(applications);
    }



    @Operation(summary = "Update an application")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApplicationFormResponseDTO> updateApplication(
            @PathVariable UUID id,
            @RequestBody @Valid ApplicationFormRequestDTO dto) {
        ApplicationFormResponseDTO updated = applicationFormService.updateApplication(id, dto);
        return ResponseEntity.ok(updated);
    }


    @Operation(summary = "Delete an application by ID")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteApplication(@PathVariable UUID id) {
        applicationFormService.deleteApplicationById(id);
    }

    @Operation(summary = "Submit a draft application")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @PostMapping("/{id}/submit")
    public ResponseEntity<Void> submitApplication(@PathVariable UUID id) {
        applicationFormService.submitApplication(id);
        return ResponseEntity.ok().build();
    }

    // ApplicationFormController.java
    @Operation(summary = "Get applications by status (DRAFT, SUBMITTED, etc.)")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ApplicationFormResponseDTO>> getApplicationsByStatus(@PathVariable ApplicationStatus status) {
        List<ApplicationFormResponseDTO> responses = applicationFormService.getApplicationsByStatus(status);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get recently submitted applications")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @GetMapping("/recent")
    public ResponseEntity<List<ApplicationFormResponseDTO>> getRecentApplications(
            @RequestParam(defaultValue = "10") int limit) {
        List<ApplicationFormResponseDTO> apps = applicationFormService.getRecentSubmittedApplications(limit);
        return ResponseEntity.ok(apps);
    }

    @Operation(summary = "Update application status manually")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @PutMapping("/{id}/{status}")
    public ResponseEntity<ApplicationFormResponseDTO> updateStatus(
            @PathVariable UUID id,
            @RequestParam ApplicationStatus status
    ) {
        ApplicationFormResponseDTO updated = applicationFormService.updateApplicationStatus(id, status);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Get applications by student ID")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @GetMapping("/students/{studentId}")
    public ResponseEntity<List<ApplicationFormResponseDTO>> getApplicationsByStudentId(
            @PathVariable UUID studentId) {
        List<ApplicationFormResponseDTO> applications = applicationFormService.getApplicationsByStudentId(studentId);
        return ResponseEntity.ok(applications);
    }

    @Operation(summary = "Get applications by university ID")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @GetMapping("/universities/{universityId}")
    public ResponseEntity<List<ApplicationFormResponseDTO>> getApplicationsByUniversityId(
            @PathVariable UUID universityId) {
        List<ApplicationFormResponseDTO> applications = applicationFormService.getApplicationsByUniversityId(universityId);
        return ResponseEntity.ok(applications);
    }

    @Operation(summary = "Get paginated applications by university ID")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @GetMapping("/universities/{universityId}/paged")
    public ResponseEntity<Page<ApplicationFormResponseDTO>> getPagedApplicationsByUniversityId(
            @PathVariable UUID universityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ApplicationFormResponseDTO> applications = applicationFormService.getPagedApplicationsByUniversityId(universityId, page, size);
        return ResponseEntity.ok(applications);
    }

    @GetMapping("/validate")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<ApplicationValidationResponseDTO> validateStudent(
            @RequestParam UUID applicationId) {

        boolean valid = applicationFormService.validateApplication(applicationId);
        String message = valid ? "Application ID is valid" : "Invalid Application ID";

        return ResponseEntity.ok(new ApplicationValidationResponseDTO(valid, message, applicationId));
    }

    @GetMapping("/validate/{applicationId}/student/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<Boolean> validateApplicationForStudent(
            @PathVariable UUID applicationId,
            @PathVariable UUID studentId) {

        boolean isValid = applicationFormService.isApplicationMappedToStudent(applicationId, studentId);
        return ResponseEntity.ok(isValid); // ✅ returns just true/false, not JSON
    }


    //feign client endpoint
//    @GetMapping("/{id}/details")
//    @Operation(summary = "Get application with student details")
//    public ResponseEntity<ApplicationWithStudentDTO> getApplicationWithStudentDetails(@PathVariable UUID id) {
//        return ResponseEntity.ok(applicationFormService.getApplicationWithStudent(id));
//    }


}