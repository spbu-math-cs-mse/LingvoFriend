package com.lingvoFriend.backend.Repositories;

import com.lingvoFriend.backend.models.RoleModel;

import org.springframework.data.mongodb.repository.MongoRepository;

// connects RoleModel and database

public interface RoleRepository extends MongoRepository<RoleModel, String> {
    Boolean existsByRoleName(String roleName);

    RoleModel findByRoleName(String roleName);
}
