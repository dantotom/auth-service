package it.dtd.note.dto.annotation.validator;

import it.dtd.note.dto.annotation.IsPresent;
import it.dtd.note.repository.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IsEmailPresentValidator implements ConstraintValidator<IsPresent, String> {
    private final UserRepository userRepository;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isBlank()) {
            return true;
        }
        return userRepository.existsByEmail(email);
    }
}
