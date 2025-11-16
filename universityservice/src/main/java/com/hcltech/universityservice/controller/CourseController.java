package com.hcltech.universityservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.hcltech.universityservice.dto.CourseRequestDTO;
import com.hcltech.universityservice.dto.CourseResponseDTO;
import com.hcltech.universityservice.service.CourseService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST controller for managing university courses.
 * <p>
 * This controller provides comprehensive course management functionality including:
 * <ul>
 *   <li>Creating courses for universities (requires ADMIN role)</li>
 *   <li>Retrieving course information with pagination</li>
 *   <li>Querying courses by university</li>
 *   <li>Updating course details (requires ADMIN role)</li>
 *   <li>Deleting courses (requires ADMIN role)</li>
 * </ul>
 * </p>
 * <p>
 * Course read operations are publicly accessible, while create, update,
 * and delete operations require authentication and ADMIN role authorization.
 * </p>
 *
 * @author HCL Tech
 * @version 1.0
 * @since 1.0
 * @see CourseService
 * @see CourseRequestDTO
 * @see CourseResponseDTO
 */
@RestController
@RequestMapping("/api/courses")
@Tag(name = "Courses", description = "Course-related operations")
@RequiredArgsConstructor
public class CourseController {

	/**
	 * Service layer for course business logic.
	 */
	private final CourseService courseService;

	@Operation(summary = "Create course for a university", description = "Create a new course for the University. Requires CREATE_COURSES permission for sub-admins")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Course created"),
			@ApiResponse(responseCode = "400", description = "Invalid input"),
			@ApiResponse(responseCode = "401", description = "Unauthorized access"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	@PostMapping("/{universityId}")
	@PreAuthorize("hasRole('ADMIN')")
	public CourseResponseDTO create(@PathVariable UUID universityId, @RequestBody CourseRequestDTO courseRequestDTO) {
		CourseResponseDTO created = courseService.create(universityId, courseRequestDTO);
		return created;
	}

	@Operation(summary = "Get all courses with pagination", description = "Retrieve a paginated list of all courses in the system")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Courses retrieved successfully"),
			@ApiResponse(responseCode = "401", description = "Unauthorized access"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	@GetMapping
	public ResponseEntity<?> getAllCourses(@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int limit) {

		List<CourseResponseDTO> courses = courseService.getAllCourses(page, limit);

		long totalCount = courseService.countCourses();
		int totalPages = (int) Math.ceil((double) totalCount / limit);

		Map<String, Object> response = new HashMap<>();
		response.put("success", true);
		response.put("count", totalCount);
		response.put("pagination", Map.of("page", page, "limit", limit, "totalPages", totalPages));
		response.put("data", courses);

		return ResponseEntity.ok(response);
	}

	@Operation(summary = "Get courses by university", description = "Retrieve all courses associated with a specific university")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "University courses retrieved successfully"),
			@ApiResponse(responseCode = "404", description = "University not found"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	@GetMapping("/university/{universityId}")
	public List<CourseResponseDTO> getByUniversity(@PathVariable UUID universityId) {
		return courseService.getCoursesByUniversity(universityId);
	}

	@Operation(summary = "Get course by ID", description = "Retrieve detailed information about a specific course using its unique identifier")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Course details retrieved successfully"),
			@ApiResponse(responseCode = "404", description = "Course not found"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	@GetMapping("/{id}")
	public CourseResponseDTO getCourseById(@PathVariable UUID id) {
		return courseService.getCourseById(id);
	}

	@Operation(summary = "Update course", description = "Update an existing course's information. Requires UPDATE_COURSES permission for sub-admins")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Course updated successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid input data"),
			@ApiResponse(responseCode = "401", description = "Unauthorized access"),
			@ApiResponse(responseCode = "403", description = "Insufficient permissions"),
			@ApiResponse(responseCode = "404", description = "Course not found"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public CourseResponseDTO updateCourse(@PathVariable UUID id, @RequestBody CourseRequestDTO courseRequestDTO) {
		return courseService.update(id, courseRequestDTO);
	}

	@Operation(summary = "Delete course", description = "Permanently delete a course from the system. Requires DELETE_COURSES permission for sub-admins")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Course deleted successfully"),
			@ApiResponse(responseCode = "401", description = "Unauthorized access"),
			@ApiResponse(responseCode = "403", description = "Insufficient permissions"),
			@ApiResponse(responseCode = "404", description = "Course not found"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public void deleteCourse(@PathVariable UUID id) {
		courseService.delete(id);
	}
}