package com.hcltech.universityservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA configuration class that enables auditing capabilities.
 * <p>
 * This configuration enables automatic population of audit fields
 * (such as createdAt and updatedAt timestamps) in JPA entities.
 * Entities annotated with {@code @EntityListeners(AuditingEntityListener.class)}
 * will automatically have their audit fields populated on creation and update.
 * </p>
 *
 * @author HCL Tech
 * @version 1.0
 * @since 1.0
 * @see org.springframework.data.jpa.domain.support.AuditingEntityListener
 */
@EnableJpaAuditing
@Configuration
public class JpaConfig {
}