package com.biblioteck.api.config;

import com.biblioteck.api.model.User;
import com.biblioteck.api.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UserRepository repository) {
        return args -> {
            if (repository.count() == 0) { // Solo si la base está vacía
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword("admin");
                admin.setRole("ADMIN");
                
                User lector = new User();
                lector.setUsername("lector");
                lector.setPassword("1234");
                lector.setRole("LECTOR");

                repository.save(admin);
                repository.save(lector);
                System.out.println("✅ Usuarios iniciales creados en MongoDB");
            }
        };
    }
}