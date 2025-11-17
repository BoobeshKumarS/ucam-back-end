package com.hcltech.studentservice.controller;

import java.util.List;
import java.util.UUID;

import com.hcltech.studentservice.dto.StudentUpdateDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.hcltech.studentservice.dto.StudentRequestDTO;
import com.hcltech.studentservice.dto.StudentResponseDTO;
import com.hcltech.studentservice.service.StudentService;

import jakarta.validation.Valid;

/**
 * REST Controller for managing student operations such as registration,
 * retrieval, update, and deletion.
 * 
 * Provides end-points for both ADMIN and STUDENT roles with appropriate
 * authorization checks.
 */
@RestController
@RequestMapping("api/students")
public class StudentController {

    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);

    @Autowired
    private StudentService studentService;

    /**
     * Registers a new student.
     *
     * @param studentRequest the student details to register
     * @return ResponseEntity containing the registered student details
     */
    @PostMapping("/register")
    public ResponseEntity<StudentResponseDTO> registerStudent(@Valid @RequestBody StudentRequestDTO studentRequest) {
        logger.info("Registering new student: {}", studentRequest.getFirstName());
        StudentResponseDTO studentResponse = studentService.registerStudent(studentRequest);
        logger.debug("Student registered successfully with ID: {}", studentResponse.getId());
        return new ResponseEntity<>(studentResponse, HttpStatus.CREATED);
    }

    /**
     * Retrieves a student by ID. Accessible only by ADMIN role.
     *
     * @param id the UUID of the student
     * @return ResponseEntity containing student details
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StudentResponseDTO> getStudentById(@PathVariable UUID id) {
        logger.info("Fetching student details for ID: {}", id);
        StudentResponseDTO studentResponse = studentService.getStudentById(id);
        logger.debug("Student details retrieved: {}", studentResponse);
        return new ResponseEntity<>(studentResponse, HttpStatus.OK);
    }

    /**
     * Retrieves all students. Accessible only by ADMIN role.
     *
     * @return ResponseEntity containing list of all students
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<StudentResponseDTO>> getAllStudents() {
        logger.info("Fetching all students");
        List<StudentResponseDTO> students = studentService.getAllStudents();
        logger.debug("Total students fetched: {}", students.size());
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    /**
     * Updates student details. Accessible only by STUDENT role.
     *
     * @param id             the UUID of the student to update
     * @param studentUpdate the updated student details
     * @return ResponseEntity containing updated student details
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<StudentResponseDTO> updateStudent(@PathVariable UUID id,
            @Valid @RequestBody StudentUpdateDTO studentUpdate) {
        logger.info("Updating student with ID: {}", id);
        StudentResponseDTO studentResponse = studentService.updateStudent(id, studentUpdate);
        logger.debug("Student updated successfully: {}", studentResponse);
        return new ResponseEntity<>(studentResponse, HttpStatus.OK);
    }

    /**
     * Deletes a student by ID. Accessible only by STUDENT role.
     *
     * @param id the UUID of the student to delete
     * @return ResponseEntity with no content
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> deleteStudent(@PathVariable UUID id) {
        logger.warn("Deleting student with ID: {}", id);
        studentService.deleteStudent(id);
        logger.info("Student deleted successfully");
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves the currently authenticated student's details.
     *
     * @param authentication the authentication object containing user details
     * @return ResponseEntity containing current student details
     */
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('STUDENT')")
    public ResponseEntity<StudentResponseDTO> getCurrentStudent(Authentication authentication) {
        logger.info("Fetching current student details for user: {}", authentication.getName());
        StudentResponseDTO studentResponse = studentService.getCurrentStudent(authentication);
        logger.debug("Current student details: {}", studentResponse);
        return ResponseEntity.ok(studentResponse);
    }
}