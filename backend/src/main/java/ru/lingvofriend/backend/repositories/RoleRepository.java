package ru.lingvofriend.backend.repositories;

import ru.lingvofriend.backend.model.RoleModel;

import org.springframework.data.mongodb.repository.MongoRepository;

// connects RoleModel and database

public interface RoleRepository extends MongoRepository<RoleModel, String> {
    Boolean existsByRoleName(String roleName);

    RoleModel findByRoleName(String roleName);
}
