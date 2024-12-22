package ru.lingvofriend.backend.repositories;

import ru.lingvofriend.backend.model.UserModel;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

// connects UserModel and database

public interface UserRepository extends MongoRepository<UserModel, String> {
    boolean existsByUsername(String username);

    Optional<UserModel> findByUsername(String username);
}
