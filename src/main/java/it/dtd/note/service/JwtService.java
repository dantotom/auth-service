package it.dtd.note.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import it.dtd.note.model.User;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Date;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final ResourceLoader resourceLoader;
    @Value("${security.jwt.private-key-path}")
    private String privateKeyLocation;
    @Value("${security.jwt.public-key-path}")
    private String publicKeyLocation;
    @Value("${security.jwt.issuer}")
    private String issuer;
    @Value("${security.jwt.access-token-ttl}")
    private long tokenTtl;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    @PostConstruct
    void init() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        this.privateKey = (PrivateKey) loadKey(privateKeyLocation, true);
        this.publicKey = (PublicKey) loadKey(publicKeyLocation, false);
    }

    private Key loadKey(String location, boolean isPrivate) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        String keyPem = readPem(location);
        byte[] bytes = Decoders.BASE64.decode(keyPem);
        if (isPrivate) {
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(bytes);
            return KeyFactory.getInstance("RSA").generatePrivate(pkcs8EncodedKeySpec);
        } else {
            X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
            return KeyFactory.getInstance("RSA").generatePublic(spec);
        }

    }

    private String readPem(final String path) throws IOException {
        Resource resource = resourceLoader.getResource(path);
        String content = new String(resource.getInputStream().readAllBytes());
        return content
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
    }


    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        List<String> roles = (user.getRole() != null)
                ? List.of(user.getRole().name())
                : Collections.emptyList();
        return Jwts.builder()
                .setIssuer(issuer)
                .setSubject(user.getEmail())
                .claim("email", user.getEmail())
                .claim("roles", roles)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(tokenTtl)))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }


    public Jws<Claims> parse(String jwt) {
        return Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(jwt);
    }
}
