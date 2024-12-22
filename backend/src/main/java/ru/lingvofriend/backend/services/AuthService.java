package ru.lingvofriend.backend.services;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import ru.lingvofriend.backend.repositories.RoleRepository;
import ru.lingvofriend.backend.repositories.UserRepository;
import ru.lingvofriend.backend.security.JwtGenerator;
import ru.lingvofriend.backend.dto.AuthResponseDto;
import ru.lingvofriend.backend.dto.AuthUserDto;
import ru.lingvofriend.backend.dto.TelegramAuthDto;
import ru.lingvofriend.backend.model.UserModel;
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

    public ResponseEntity<?> telegramLogin(TelegramAuthDto telegramAuth, HttpServletResponse response) {
        /* if (!telegramAuthService.checkTelegramAuthorization(telegramAuth)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponseDto(null, "INVALID_AUTHORIZATION"));
        } Need to check hash differently, task for later */

        String username = "telegram_" + telegramAuth.getId();

        // Create user if doesn't exist
        if (!userRepository.existsByUsername(username)) {
            // Generate a fixed password based on user ID and a secret key
            String fixedPassword = generateTelegramUserPassword(telegramAuth.getId());

            UserModel userModel = new UserModel(
                    username,
                    passwordEncoder.encode(fixedPassword),
                    new ArrayList<>(List.of(roleRepository.findByRoleName("USER"))),
                    new ArrayList<>()
            );
            userRepository.save(userModel);
        }

        String fixedPassword = generateTelegramUserPassword(telegramAuth.getId());

        // Create authentication token
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, fixedPassword)
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

    private String generateTelegramUserPassword(String telegramId) {
        String secretKey = "Kj9@mP#2$pL5vN8*qR4wX7!hC3fA6tY9%gU2nB5$mK8@dW4"; // Add to the .env later

        String combined = telegramId + secretKey;

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(combined.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate password", e);
        }
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
