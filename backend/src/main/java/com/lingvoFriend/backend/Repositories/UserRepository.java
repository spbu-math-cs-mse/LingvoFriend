package com.lingvoFriend.backend.Repositories;

import com.lingvoFriend.backend.Services.AuthService.models.UserModel;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

// connects UserModel and database

public interface UserRepository extends MongoRepository<UserModel, String> {
    boolean existsByUsername(String username);

    Optional<UserModel> findByUsername(String username);
}
