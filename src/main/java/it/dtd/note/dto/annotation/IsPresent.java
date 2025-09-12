package it.dtd.note.dto.annotation;

import it.dtd.note.dto.annotation.validator.IsEmailPresentValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = {IsEmailPresentValidator.class})
@Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface IsPresent {
    String message() default "Utente non trovato";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
