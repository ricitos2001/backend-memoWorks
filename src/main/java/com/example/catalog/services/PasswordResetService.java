package com.example.catalog.services;

import com.example.catalog.domain.dto.PasswordForgotRequestDTO;
import com.example.catalog.domain.entities.PasswordResetToken;
import com.example.catalog.domain.entities.User;
import com.example.catalog.repositories.PasswordResetTokenRepository;
import com.example.catalog.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class PasswordResetService {
    private static final Logger logger = LoggerFactory.getLogger(PasswordResetService.class);

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${app.password-reset.token-expiration-minutes:60}")
    private long tokenExpirationMinutes;

    public PasswordResetService(UserRepository userRepository, PasswordResetTokenRepository tokenRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void requestPasswordReset(String email) {
        // Responder igual si existe o no para no filtrar usuarios
        Optional<User> optionalUser = userRepository.findByEmail(email.toLowerCase());
        if (optionalUser.isEmpty()) {
            // No revelar existencia: simplemente loggear y retornar
            logger.info("Password reset requested for non-existing email: {}", email);
            return;
        }

        User user = optionalUser.get();

        // generar token seguro
        String rawToken = generateToken();
        String tokenHash = hash(rawToken);

        Instant now = Instant.now();
        PasswordResetToken token = PasswordResetToken.builder()
                .tokenHash(tokenHash)
                .user(user)
                .createdAt(now)
                .expiresAt(now.plus(tokenExpirationMinutes, ChronoUnit.MINUTES))
                .used(false)
                .build();

        tokenRepository.save(token);

        // Enviar correo (en dev: loggear el token y link)
        String resetLink = String.format("%s?token=%s", getFrontendResetUrl(), rawToken);
        logger.info("Password reset requested for user {}. Reset link (DEV): {}", user.getEmail(), resetLink);
    }

    public boolean validateToken(String rawToken) {
        String tokenHash = hash(rawToken);
        Optional<PasswordResetToken> optionalToken = tokenRepository.findByTokenHash(tokenHash);
        if (optionalToken.isEmpty()) return false;
        PasswordResetToken token = optionalToken.get();
        if (token.isUsed()) return false;
        if (token.getExpiresAt().isBefore(Instant.now())) return false;
        return true;
    }

    public void resetPassword(String rawToken, String newPassword) {
        String tokenHash = hash(rawToken);
        PasswordResetToken token = tokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido o expirado"));

        if (token.isUsed()) throw new IllegalArgumentException("Token ya utilizado");
        if (token.getExpiresAt().isBefore(Instant.now())) throw new IllegalArgumentException("Token expirado");

        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        // guardar usuario
        userRepository.save(user);

        // marcar token como usado y guardar
        token.setUsed(true);
        tokenRepository.save(token);

        // invalidar otros tokens del usuario (optional): borrar tokens no usados
        // tokenRepository.deleteByUserAndUsedFalse(user); // no implementado
    }

    public void purgeExpiredTokens() {
        tokenRepository.deleteByExpiresAtBefore(Instant.now());
    }

    private String generateToken() {
        // UUID + random bytes -> base64url
        byte[] random = new byte[24];
        secureRandom.nextBytes(random);
        String uuid = UUID.randomUUID().toString();
        String combined = uuid + Base64.getUrlEncoder().withoutPadding().encodeToString(random);
        return combined;
    }

    private String hash(String raw) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private String getFrontendResetUrl() {
        // se puede leer de properties, por ahora usar valor por defecto
        return "http://localhost:4200/reset-password";
    }
}

