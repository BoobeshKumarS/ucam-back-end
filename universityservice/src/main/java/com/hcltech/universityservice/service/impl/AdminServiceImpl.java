package com.hcltech.universityservice.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.hcltech.universityservice.dto.AdminRequestDTO;
import com.hcltech.universityservice.dto.AdminUpdateDTO;
import com.hcltech.universityservice.dto.AdminResponseDTO;
import com.hcltech.universityservice.entity.Admin;
import com.hcltech.universityservice.exception.UserNotFoundException;
import com.hcltech.universityservice.feign.AuthClient;
import com.hcltech.universityservice.feign.UserRegisterRequestDTO;
import com.hcltech.universityservice.feign.UserRole;
import com.hcltech.universityservice.repository.AdminRepository;
import com.hcltech.universityservice.service.AdminService;
import com.hcltech.universityservice.util.AdminConverter;

@Service
public class AdminServiceImpl implements AdminService {

	@Autowired
	private AdminRepository adminRepository;

	@Autowired
	private AdminConverter adminConverter;
	
	@Autowired
	private AuthClient authClient;

	@Override
	public AdminResponseDTO registerAdmin(AdminRequestDTO adminRequest) {
		UserRegisterRequestDTO registerRequest = new UserRegisterRequestDTO();
		String username = adminRequest.getEmail().split("@")[0] // Use email prefix as username
                .toLowerCase()
                .replaceAll("[^a-z0-9]", "_");
		
		registerRequest.setUsername(username);
		registerRequest.setEmail(adminRequest.getEmail());
		registerRequest.setPassword(adminRequest.getPassword());
		registerRequest.getRoles().add(UserRole.ADMIN);
		
		authClient.registerUser(registerRequest);
		
		Admin savedAdmin = adminRepository.save(adminConverter.toEntity(adminRequest));
		return adminConverter.toResponse(savedAdmin);
	}

	@Override
	public AdminResponseDTO getAdminById(UUID id) {
		Admin admin = adminRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Admin", id));
		return adminConverter.toResponse(admin);
	}

	@Override
	public List<AdminResponseDTO> getAllAdmins() {
		return adminRepository.findAll().stream().map(adminConverter::toResponse)
				.toList();
	}

	@Override
	public AdminResponseDTO updateAdmin(UUID id, AdminUpdateDTO adminUpdate) {
		Admin admin = adminRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Admin", id));
		admin.setFirstName(adminUpdate.getFirstName());
        admin.setLastName(adminUpdate.getLastName());
        admin.setDateOfBirth(adminUpdate.getDateOfBirth());
        admin.setPhoneNumber(adminUpdate.getPhoneNumber());
        admin.setGender(adminUpdate.getGender());
        admin.setNationality(adminUpdate.getNationality());
        
		adminRepository.save(admin);
		return adminConverter.toResponse(admin);
	}

	@Override
	public void deleteAdmin(UUID id) {
		Admin admin = adminRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Admin", id));
		adminRepository.deleteById(admin.getId());
	}

	@Override
	public AdminResponseDTO getCurrentAdmin(Authentication authentication) {
		if (authentication == null)
			return null;
		String email = authentication.getName();
		
	    Admin admin = adminRepository.findByEmail(email)
	            .orElseThrow(() -> new UserNotFoundException("Admin not found", "email", email));

	    return adminConverter.toResponse(admin);
	}

}
