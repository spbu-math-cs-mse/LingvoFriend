package com.lingvoFriend.backend.Controllers;

import com.lingvoFriend.backend.Repositories.RoleRepository;
import com.lingvoFriend.backend.Repositories.UserRepository;
import com.lingvoFriend.backend.dto.LoginDto;
import com.lingvoFriend.backend.dto.RegisterDto;
import com.lingvoFriend.backend.models.UserModel;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// here is the logic and mapping for AuthControllers

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
@CrossOrigin
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) {
        if (userRepository.existsByUsername(registerDto.username)) {
            return new ResponseEntity<>("Username is taken", HttpStatus.BAD_REQUEST);
        }

        UserModel userModel =
                new UserModel(
                        registerDto.username,
                        passwordEncoder.encode(registerDto.password),
                        List.of(roleRepository.findByRoleName("USER")));

        userRepository.save(userModel);

        return new ResponseEntity<>("Successfully registered", HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto) {
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginDto.username, loginDto.password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return new ResponseEntity<>("Successfully logged in", HttpStatus.OK);
    }
}
