package it.dtd.note.service;

import it.dtd.note.dto.*;
import it.dtd.note.enumeration.UserRole;
import it.dtd.note.enumeration.UserStatus;
import it.dtd.note.model.RefreshToken;
import it.dtd.note.model.User;
import it.dtd.note.repository.RefreshTokenRepository;
import it.dtd.note.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.token.Sha512DigestUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    @Value("${security.jwt.refresh-token-ttl}")
    private long refreshTokenTtl;

    @Transactional
    public void register(final RegisterRequest registerRequest) {
        User user = new User();
        user.setEmail(registerRequest.email());
        user.setPasswordHash(passwordEncoder.encode(registerRequest.password()));
        user.setFullName(registerRequest.fullName());
        user.setStatus(UserStatus.ACTIVE);
        user.setRole(UserRole.USER);
        userRepository.save(user);
    }

    @Transactional
    public LoginResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.email()).orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));
        if (!passwordEncoder.matches(req.password(), user.getPasswordHash()))
            throw new IllegalArgumentException("Accesso negato - email o password errata");
        user.setLastLoginAt(Instant.now());
        userRepository.save(user);
        String access = jwtService.generateAccessToken(user);
        String refresh = java.util.UUID.randomUUID().toString();
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(Sha512DigestUtils.shaHex(refresh));
        refreshToken.setIssuedAt(Instant.now());
        refreshToken.setExpiresAt(Instant.now().plusSeconds(refreshTokenTtl));

        refreshTokenRepository.save(refreshToken);
        return new LoginResponse().setAccessToken(access).setEmail(user.getEmail()).setRole(user.getRole().name()).setRefreshToken(refresh);
    }

    @Transactional
    public AuthResponse refresh(RefreshRequest req, String ip, String ua) {
        String h = Sha512DigestUtils.shaHex(req.refreshToken());
        RefreshToken current = refreshTokenRepository.findByTokenHash(h).orElseThrow(() -> new IllegalArgumentException("Token non valido"));
        if (current.getRevokedAt() != null || current.getExpiresAt().isBefore(Instant.now()))
            throw new IllegalArgumentException("Token non valido");


        current.setRevokedAt(Instant.now());
        User u = current.getUser();
        String access = jwtService.generateAccessToken(u);
        String next = UUID.randomUUID().toString();


        RefreshToken rt = new RefreshToken();
        rt.setUser(u);
        rt.setTokenHash(Sha512DigestUtils.shaHex(next));
        rt.setIssuedAt(Instant.now());
        rt.setExpiresAt(Instant.now().plusSeconds(refreshTokenTtl));
        rt.setRotatedFrom(current.getId());
        refreshTokenRepository.save(rt);


        return new AuthResponse(access, next);
    }


    @Transactional
    public void logout(RefreshRequest req) {
        refreshTokenRepository.findByTokenHash(Sha512DigestUtils.shaHex(req.refreshToken()))
                .ifPresent(t -> t.setRevokedAt(Instant.now()));
    }

}
