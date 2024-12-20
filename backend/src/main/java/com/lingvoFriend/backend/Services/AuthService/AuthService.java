package com.lingvoFriend.backend.Services.AuthService;

import java.util.ArrayList;
import java.util.List;

import com.lingvoFriend.backend.Repositories.RoleRepository;
import com.lingvoFriend.backend.Repositories.UserRepository;
import com.lingvoFriend.backend.Security.JwtGenerator;
import com.lingvoFriend.backend.Services.AuthService.dto.AuthResponseDto;
import com.lingvoFriend.backend.Services.AuthService.dto.AuthUserDto;
import com.lingvoFriend.backend.Services.AuthService.dto.TelegramAuthDto;
import com.lingvoFriend.backend.Services.AuthService.models.UserModel;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;

import com.lingvoFriend.backend.Repositories.RoleRepository;
import com.lingvoFriend.backend.Repositories.UserRepository;
import com.lingvoFriend.backend.Security.JwtGenerator;
import com.lingvoFriend.backend.Services.AuthService.dto.AuthResponseDto;
import com.lingvoFriend.backend.Services.AuthService.dto.AuthUserDto;
import com.lingvoFriend.backend.Services.AuthService.models.UserModel;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtGenerator jwtGenerator;
    private final TelegramAuthService telegramAuthService;

    public ResponseEntity<?> register(AuthUserDto authUserDto, HttpServletResponse response) {
        if (userRepository.existsByUsername(authUserDto.getUsername())) {
            return new ResponseEntity<>("USERNAME_TAKEN", HttpStatus.BAD_REQUEST);
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

        if (!userRepository.existsByUsername(authUserDto.getUsername())) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new AuthResponseDto(null, "WRONG_USERNAME"));
        }
        try {
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

            return ResponseEntity.ok(new AuthResponseDto(token, null));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new AuthResponseDto(null, "WRONG_PASSWORD"));
        }
    }

    public ResponseEntity<AuthResponseDto> telegramLogin(TelegramAuthDto telegramAuth, HttpServletResponse response) {
        if (!telegramAuthService.checkTelegramAuthorization(telegramAuth)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponseDto(null, "INVALID_AUTHORIZATION"));
        }

        String username = "telegram_" + telegramAuth.getId();

        // Create user if doesn't exist
        if (!userRepository.existsByUsername(username)) {
            UserModel userModel = new UserModel(
                    username,
                    passwordEncoder.encode(telegramAuth.getHash()), // Use hash as password
                    new ArrayList<>(List.of(roleRepository.findByRoleName("USER"))),
                    new ArrayList<>()
            );
            userRepository.save(userModel);
        }

        // Create authentication token
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, telegramAuth.getHash())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtGenerator.generateToken(authentication);

        Cookie cookie = new Cookie("__Host-auth-token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) JwtGenerator.JWT_EXPIRATION_TIME.toSeconds());

        response.addCookie(cookie);

        return ResponseEntity.ok(new AuthResponseDto(token, null));
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
