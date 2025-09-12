package it.dtd.note.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Table(name = "refresh_token")
@Entity
@Getter
@Setter
@ToString
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;
    @Column(nullable = false)
    private String tokenHash;
    private Instant issuedAt;
    private Instant expiresAt;
    private UUID rotatedFrom;
    private Instant revokedAt;

}
