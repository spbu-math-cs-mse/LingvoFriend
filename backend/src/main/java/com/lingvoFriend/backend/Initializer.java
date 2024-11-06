package com.lingvoFriend.backend;

import com.lingvoFriend.backend.Repositories.RoleRepository;
import com.lingvoFriend.backend.Repositories.UserRepository;
import com.lingvoFriend.backend.models.RoleModel;
import com.lingvoFriend.backend.models.UserModel;

import jakarta.annotation.PostConstruct;

import lombok.AllArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

// here we just fill up the database with default user and roles in case its empty

@Component
@AllArgsConstructor
public class Initializer {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    void defaultRolesInit() {
        if (!roleRepository.existsByRoleName("USER")) {
            roleRepository.save(new RoleModel("USER"));
        }

        if (!roleRepository.existsByRoleName("ADMIN")) {
            roleRepository.save(new RoleModel("ADMIN"));
        }
    }

    @PostConstruct
    void defaultUserInit() {
        if (!userRepository.existsByUsername("admin")) {
            List<RoleModel> ListOfDefaultRoles =
                    List.of(
                            roleRepository.findByRoleName("ADMIN"),
                            roleRepository.findByRoleName("USER"));

            userRepository.save(new UserModel("admin", passwordEncoder.encode("pass"), ListOfDefaultRoles));
        }
    }
}
