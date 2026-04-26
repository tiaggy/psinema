package org.psi.psinema.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.psi.psinema.domain.user.Role;
import org.psi.psinema.domain.user.User;
import org.psi.psinema.domain.user.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        createUserIfAbsent("admin@psinema.com", "admin123", Role.ADMIN, "Admin", "User");
        createUserIfAbsent("employee@psinema.com", "employee123", Role.EMPLOYEE, "Staff", "Member");
        createUserIfAbsent("customer@psinema.com", "customer123", Role.CUSTOMER, "Test", "Customer");
    }

    private void createUserIfAbsent(String email, String password, Role role, String firstName, String lastName) {
        if (!userRepository.existsByEmail(email)) {
            userRepository.save(User.builder()
                    .email(email)
                    .passwordHash(passwordEncoder.encode(password))
                    .role(role)
                    .firstName(firstName)
                    .lastName(lastName)
                    .build());
            log.info("Created default {} user: {}", role, email);
        }
    }
}
