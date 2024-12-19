package com.lingvoFriend.backend.Services.AuthService;

import com.lingvoFriend.backend.Repositories.RoleRepository;
import com.lingvoFriend.backend.Repositories.UserRepository;
import com.lingvoFriend.backend.Security.JwtGenerator;
import com.lingvoFriend.backend.Services.AuthService.dto.AuthResponseDto;
import com.lingvoFriend.backend.Services.AuthService.dto.AuthUserDto;
import com.lingvoFriend.backend.Services.AuthService.models.UserModel;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtGenerator jwtGenerator;

    public ResponseEntity<?> register(AuthUserDto authUserDto, HttpServletResponse response) {
        if (userRepository.existsByUsername(authUserDto.getUsername())) {
            return new ResponseEntity<>("Username is taken", HttpStatus.BAD_REQUEST);
        }

        UserModel userModel =
                new UserModel(
                        authUserDto.getUsername(),
                        passwordEncoder.encode(authUserDto.getPassword()),
                        new ArrayList<>(List.of(roleRepository.findByRoleName("USER"))),
                        new ArrayList<>());

        userRepository.save(userModel);

        return login(authUserDto, response);
    }

    public ResponseEntity<AuthResponseDto> login(
            @RequestBody AuthUserDto authUserDto, HttpServletResponse response) {
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                authUserDto.getUsername(), authUserDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtGenerator.generateToken(authentication);

        Cookie cookie = new Cookie("__Host-auth-token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) JwtGenerator.JWT_EXPIRATION_TIME.toSeconds());

        response.addCookie(cookie);

        return ResponseEntity.ok(new AuthResponseDto(token));
    }

    public ResponseEntity<String> validateToken(String token) {
        if (StringUtils.hasText(token) && jwtGenerator.validateToken(token)) {
            return ResponseEntity.ok().body("Token is valid");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    public String getUsernameFromToken(String token) {
        return jwtGenerator.getUsernameFromToken(token);
    }
}
