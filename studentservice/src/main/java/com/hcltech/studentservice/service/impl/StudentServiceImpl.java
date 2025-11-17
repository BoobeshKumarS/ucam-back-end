package com.hcltech.studentservice.service.impl;

import java.util.List;
import java.util.UUID;

import com.hcltech.studentservice.dto.StudentUpdateDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.hcltech.studentservice.dto.StudentRequestDTO;
import com.hcltech.studentservice.dto.StudentResponseDTO;
import com.hcltech.studentservice.entity.Student;
import com.hcltech.studentservice.exception.UserNotFoundException;
import com.hcltech.studentservice.feign.AuthClient;
import com.hcltech.studentservice.feign.UserRegisterRequestDTO;
import com.hcltech.studentservice.feign.UserRole;
import com.hcltech.studentservice.repository.StudentRepository;
import com.hcltech.studentservice.service.StudentService;
import com.hcltech.studentservice.util.StudentConverter;

/**
 * Implementation of {@link StudentService} that handles student-related
 * operations such as registration, retrieval, update, and deletion.
 * 
 * Integrates with external authentication service for user registration.
 */
@Service
public class StudentServiceImpl implements StudentService {

    private static final Logger studentLogger = LoggerFactory.getLogger(StudentServiceImpl.class);

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentConverter studentConverter;

    @Autowired
    private AuthClient authClient;

    /**
     * Registers a new student and creates a corresponding user in the authentication service.
     *
     * @param studentRequest the student details to register
     * @return StudentResponseDTO containing registered student details
     */
    @Override
    public StudentResponseDTO registerStudent(StudentRequestDTO studentRequest) {
        studentLogger.info("Registering new student with email: {}", studentRequest.getEmail());

        // Prepare user registration request for Auth service
        String username = studentRequest.getEmail().split("@")[0]
                .toLowerCase()
                .replaceAll("[^a-z0-9]", "_");

        UserRegisterRequestDTO registerRequest = new UserRegisterRequestDTO();
        registerRequest.setUsername(username);
        registerRequest.setEmail(studentRequest.getEmail());
        registerRequest.setPassword(studentRequest.getPassword());
        registerRequest.getRoles().add(UserRole.STUDENT);

        studentLogger.debug("Sending user registration request to Auth service: {}", registerRequest);
        authClient.registerUser(registerRequest);

        Student savedStudent = studentRepository.save(studentConverter.toEntity(studentRequest));
        studentLogger.info("Student registered successfully with ID: {}", savedStudent.getId());

        return studentConverter.toResponse(savedStudent);
    }

    /**
     * Retrieves a student by their unique ID.
     *
     * @param id the UUID of the student
     * @return StudentResponseDTO containing student details
     * @throws UserNotFoundException if student is not found
     */
    @Override
    public StudentResponseDTO getStudentById(UUID id) {
        studentLogger.info("Fetching student details for ID: {}", id);
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Student", id));
        studentLogger.debug("Student details retrieved: {}", student);
        return studentConverter.toResponse(student);
    }

    /**
     * Retrieves all students from the database.
     *
     * @return List of StudentResponseDTO containing all student details
     */
    @Override
    public List<StudentResponseDTO> getAllStudents() {
        studentLogger.info("Fetching all students from database");
        List<StudentResponseDTO> students = studentRepository.findAll()
                .stream()
                .map(studentConverter::toResponse)
                .toList();
        studentLogger.debug("Total students fetched: {}", students.size());
        return students;
    }

    /**
     * Updates student details for the given ID.
     *
     * @param id the UUID of the student to update
     * @param studentUpdate the updated student details
     * @return StudentResponseDTO containing updated student details
     * @throws UserNotFoundException if student is not found
     */
    @Override
    public StudentResponseDTO updateStudent(UUID id, StudentUpdateDTO studentUpdate) {
        studentLogger.info("Updating student with ID: {}", id);
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Student", id));

        student.setFirstName(studentUpdate.getFirstName());
        student.setLastName(studentUpdate.getLastName());
        student.setDateOfBirth(studentUpdate.getDateOfBirth());
        student.setPhoneNumber(studentUpdate.getPhoneNumber());
        student.setGender(studentUpdate.getGender());
        student.setNationality(studentUpdate.getNationality());
        student.setAddress(studentUpdate.getAddress());

        studentRepository.save(student);
        studentLogger.info("Student updated successfully: {}", student.getId());
        return studentConverter.toResponse(student);
    }

    /**
     * Deletes a student by their unique ID.
     *
     * @param id the UUID of the student to delete
     * @throws UserNotFoundException if student is not found
     */
    @Override
    public void deleteStudent(UUID id) {
        studentLogger.warn("Deleting student with ID: {}", id);
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Student", id));
        studentRepository.deleteById(student.getId());
        studentLogger.info("Student deleted successfully");
    }

    /**
     * Retrieves the currently authenticated student's details based on email.
     *
     * @param authentication the authentication object containing user details
     * @return StudentResponseDTO containing current student details, or null if authentication is missing
     * @throws UserNotFoundException if student is not found
     */
    @Override
    public StudentResponseDTO getCurrentStudent(Authentication authentication) {
        if (authentication == null) {
            studentLogger.error("Authentication object is null. Cannot fetch current student.");
            return null;
        }
        String email = authentication.getName();
        studentLogger.info("Fetching current student details for email: {}", email);

        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Student not found", "email", email));

        studentLogger.debug("Current student details retrieved: {}", student);
        return studentConverter.toResponse(student);
    }
}