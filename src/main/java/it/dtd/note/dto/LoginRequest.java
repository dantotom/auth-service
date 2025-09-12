package it.dtd.note.dto;

import it.dtd.note.dto.annotation.IsPresent;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(@IsPresent @Email String email, @NotBlank String password) {
}
