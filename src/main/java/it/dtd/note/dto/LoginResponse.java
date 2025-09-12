package it.dtd.note.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class LoginResponse {
    @Schema(description = "JWT da usare come Bearer Token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6...")
    private String accessToken;

    @Schema(description = "Tipo di token, solitamente 'Bearer'", example = "Bearer")
    private String tokenType = "Bearer";

    @Schema(description = "Email dell’utente loggato", example = "user@example.com")
    private String email;

    @Schema(description = "Ruolo dell’utente", example = "ADMIN")
    private String role;

    @Schema(description = "Nuovo refresh token, da usare per rinnovare l'access token", example = "550e8400-e29b-41d4-a716-446655440000")
    private String refreshToken;
}
