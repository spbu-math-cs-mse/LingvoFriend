package ru.lingvofriend.backend;

import ru.lingvofriend.backend.model.Message;
import ru.lingvofriend.backend.repositories.RoleRepository;
import ru.lingvofriend.backend.repositories.UserRepository;
import ru.lingvofriend.backend.model.RoleModel;
import ru.lingvofriend.backend.model.UserModel;

import jakarta.annotation.PostConstruct;

import lombok.AllArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

// here we just fill up the database with default user and roles in case its empty

@Slf4j
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
            log.info("Init users DB");
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
