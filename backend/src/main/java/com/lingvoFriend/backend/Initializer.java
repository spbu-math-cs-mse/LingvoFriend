package com.lingvoFriend.backend;

import com.lingvoFriend.backend.Services.ChatService.models.Message;
import com.lingvoFriend.backend.Repositories.RoleRepository;
import com.lingvoFriend.backend.Repositories.UserRepository;
import com.lingvoFriend.backend.Services.AuthService.models.RoleModel;
import com.lingvoFriend.backend.Services.AuthService.models.UserModel;

import jakarta.annotation.PostConstruct;

import lombok.AllArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
                    new ArrayList<>(
                            List.of(
                                    roleRepository.findByRoleName("ADMIN"),
                                    roleRepository.findByRoleName("USER")));

            List<Message> messages = new ArrayList<>();

            userRepository.save(
                    new UserModel(
                            "admin", passwordEncoder.encode("pass"), ListOfDefaultRoles, messages));
        }
    }
}
