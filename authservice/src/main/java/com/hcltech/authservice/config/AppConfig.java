package com.hcltech.authservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Application configuration class for common beans and utilities.
 *
 * <p>This configuration class defines beans that are used across the application,
 * particularly for password encoding and security-related functionality.</p>
 *
 * @author HCL Tech
 * @version 1.0
 * @since 2025-01-01
 */
@Configuration
public class AppConfig {

	/**
	 * Creates and configures a BCrypt password encoder bean.
	 *
	 * <p>BCrypt is a strong hashing algorithm that includes a salt to protect
	 * against rainbow table attacks. This encoder is used for securely hashing
	 * user passwords before storing them in the database.</p>
	 *
	 * @return a {@link PasswordEncoder} instance configured with BCrypt algorithm
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
