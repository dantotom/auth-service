package it.dtd.note.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record AuthResponse(@Schema(description = "Nuovo JWT di accesso (Bearer)", example = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...") String accessToken, @Schema(description = "Nuovo refresh token, da usare per rinnovare l'access token", example = "550e8400-e29b-41d4-a716-446655440000") String refreshToken){
}
