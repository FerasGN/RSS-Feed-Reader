package de.htwsaar.pib2021.rss_feed_reader.validators.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import de.htwsaar.pib2021.rss_feed_reader.validators.FormInputConstaintValidator;



@Documented
@Constraint(validatedBy = FormInputConstaintValidator.class)
@Target({ FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface ValidFormInput {

    String message() default "Invalid Input";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int minLength();
    int maxLength();
    boolean blank();
}
