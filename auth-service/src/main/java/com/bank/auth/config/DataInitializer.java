package com.bank.auth.config;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.bank.auth.entity.Role;
import com.bank.auth.entity.RoleType;
import com.bank.auth.repository.RoleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final RoleRepository roleRepository;
    
    @Override
    public void run(String... args) {
        initializeRoles();
    }
    
    private void initializeRoles() {
        // Create default roles if they don't exist
        createRoleIfNotExists(RoleType.ROLE_CUSTOMER, "Customer role with basic permissions");
        createRoleIfNotExists(RoleType.ROLE_ADMIN, "Administrator role with full permissions");
        createRoleIfNotExists(RoleType.ROLE_MANAGER, "Manager role with elevated permissions");
        createRoleIfNotExists(RoleType.ROLE_SUPPORT, "Support role for customer service");
        
        log.info("Roles initialized successfully");
    }
    
    private void createRoleIfNotExists(RoleType roleType, String description) {
        if (!roleRepository.existsByName(roleType)) {
            Role role = Role.builder()
                    .name(roleType)
                    .description(description)
                    .build();
            roleRepository.save(role);
            log.info("Created role: {}", roleType);
        }
    }
}