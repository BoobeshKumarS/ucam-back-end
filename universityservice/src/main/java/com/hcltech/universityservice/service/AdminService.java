package com.hcltech.universityservice.service;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;

import com.hcltech.universityservice.dto.AdminRequestDTO;
import com.hcltech.universityservice.dto.AdminUpdateDTO;
import com.hcltech.universityservice.dto.AdminResponseDTO;

public interface AdminService {
	AdminResponseDTO registerAdmin(AdminRequestDTO adminRequest);
	AdminResponseDTO getAdminById(UUID id);
	List<AdminResponseDTO> getAllAdmins();
	AdminResponseDTO updateAdmin(UUID id, AdminUpdateDTO adminUpdate);
	void deleteAdmin(UUID id);
	AdminResponseDTO getCurrentAdmin(Authentication authentication);
}
