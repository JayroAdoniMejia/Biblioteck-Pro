package com.biblioteck.api.repository; // Ajustado a tu estructura real

import com.biblioteck.api.model.User; // Importación corregida
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
}