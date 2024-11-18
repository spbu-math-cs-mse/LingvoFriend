package com.lingvoFriend.backend.Services.AuthService;

import com.lingvoFriend.backend.Repositories.RoleRepository;
import com.lingvoFriend.backend.Repositories.UserRepository;
import com.lingvoFriend.backend.Services.AuthService.dto.LoginDto;
import com.lingvoFriend.backend.Services.AuthService.dto.RegisterDto;
import com.lingvoFriend.backend.Services.AuthService.models.UserModel;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<String> register(RegisterDto registerDto) {
        if (userRepository.existsByUsername(registerDto.getUsername())) {
            return new ResponseEntity<>("Username is taken", HttpStatus.BAD_REQUEST);
        }

        UserModel userModel =
                new UserModel(
                        registerDto.getUsername(),
                        passwordEncoder.encode(registerDto.getPassword()),
                        new ArrayList<>(List.of(roleRepository.findByRoleName("USER"))),
                        new ArrayList<>());

        userRepository.save(userModel);

        return new ResponseEntity<>("Successfully registered", HttpStatus.CREATED);
    }

    public ResponseEntity<String> login(LoginDto loginDto) {
        try {
            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    loginDto.getUsername(), loginDto.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            return new ResponseEntity<>("Successfully logged in", HttpStatus.OK);
        } catch (BadCredentialsException ex) {
            return new ResponseEntity<>(
                    "User not found or incorrect password", HttpStatus.UNAUTHORIZED);
        }
    }
}
