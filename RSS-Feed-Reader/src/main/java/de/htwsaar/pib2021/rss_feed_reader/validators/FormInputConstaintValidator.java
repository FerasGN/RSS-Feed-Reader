package de.htwsaar.pib2021.rss_feed_reader.validators;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import de.htwsaar.pib2021.rss_feed_reader.validators.annotation.ValidFormInput;

public class FormInputConstaintValidator implements ConstraintValidator<ValidFormInput, String> {

    private Integer minLength;
    private Integer maxLength;
    private boolean blank;

    @Override
    public void initialize(ValidFormInput constraint) {
        this.minLength = constraint.minLength();
        this.maxLength = constraint.maxLength();
        this.blank = constraint.blank();
    }

    @Override
    public boolean isValid(String text, ConstraintValidatorContext context) {
        if (Math.abs(minLength) > Math.abs(maxLength)) {
            Integer temp = maxLength;
            maxLength = minLength;
            minLength = temp;
        }

        List<String> messages = new ArrayList<>();
        if (blank) {
            messages.add("* Length must be between " + minLength + " and " + maxLength + ".");
            String messageTemplate = messages.stream().collect(Collectors.joining("\n"));

            context.buildConstraintViolationWithTemplate(messageTemplate).addConstraintViolation()
                    .disableDefaultConstraintViolation();
            return text.length() >= minLength && text.length() <= maxLength;
        } else {
            messages.add("* Length must be between " + minLength + " and " + maxLength + ".");
            messages.add("* Field must not be empty");
            String messageTemplate = messages.stream().collect(Collectors.joining("\n"));

            context.buildConstraintViolationWithTemplate(messageTemplate).addConstraintViolation()
                    .disableDefaultConstraintViolation();
            return text.length() >= minLength && text.length() <= maxLength && !text.trim().isEmpty();
        }
    }

}
