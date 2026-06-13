package com.sulabh.auth_service.config;

import com.sulabh.auth_service.model.User;
import com.sulabh.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer
        implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        if(userRepository.findByEmail("admin@gmail.com")
                .isEmpty()) {

            User admin = new User();

            admin.setEmail("admin@gmail.com");

            admin.setPassword(
                    passwordEncoder.encode("admin123")
            );

            admin.setRole("ADMIN");

            userRepository.save(admin);
        }
    }
}