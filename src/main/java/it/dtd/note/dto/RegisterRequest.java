package it.dtd.note.dto;

import it.dtd.note.dto.annotation.IsUnique;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(@IsUnique @Email String email, @NotBlank String password, String fullName) {
}
