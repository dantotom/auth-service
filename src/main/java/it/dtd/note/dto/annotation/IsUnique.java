package it.dtd.note.dto.annotation;


import it.dtd.note.dto.annotation.validator.UniqueEmailValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = {UniqueEmailValidator.class})
@Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface IsUnique {
    String message() default "L'email esiste già";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
