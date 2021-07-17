package com.feeedify.validators;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.feeedify.validators.annotation.ValidFormInput;

public class FormInputConstaintValidator implements ConstraintValidator<ValidFormInput, String> {

	private Integer minLength;
	private Integer maxLength;
	private boolean blank;
	private boolean containsOnlyLettersAndNumbers;

	@Override
	public void initialize(ValidFormInput constraint) {
		this.minLength = constraint.minLength();
		this.maxLength = constraint.maxLength();
		this.blank = constraint.blank();
		this.containsOnlyLettersAndNumbers = constraint.containsOnlyLettersAndNumbers();
	}

	@Override
	public boolean isValid(String text, ConstraintValidatorContext context) {
		if (Math.abs(minLength) > Math.abs(maxLength)) {
			Integer temp = maxLength;
			maxLength = minLength;
			minLength = temp;
		}

		boolean textMinLength = text.length() >= minLength;
		boolean textMaxLength = text.length() <= maxLength;
		boolean textNotEmpty = !text.trim().isEmpty();
		boolean textContainsOnlyLettersAndNumbers = text.matches(("[a-zA-Z0-9]{" + minLength + "," + maxLength + "}"));

		List<String> messages = new ArrayList<>();
		if (blank) {
			messages.add("* Length must be between " + minLength + " and " + maxLength + ".");
			String messageTemplate = messages.stream().collect(Collectors.joining("\n"));

			context.buildConstraintViolationWithTemplate(messageTemplate).addConstraintViolation()
					.disableDefaultConstraintViolation();
			return textMinLength && textMaxLength;
		} else if (containsOnlyLettersAndNumbers) {
			messages.add("* Length must be between " + minLength + " and " + maxLength + ".");
			messages.add("* Field must not be empty.");
			messages.add("* Field must only contain letters and numbers.");
			String messageTemplate = messages.stream().collect(Collectors.joining("\n"));

			context.buildConstraintViolationWithTemplate(messageTemplate).addConstraintViolation()
					.disableDefaultConstraintViolation();
			return textMinLength && textMaxLength && textNotEmpty & textContainsOnlyLettersAndNumbers;
		} else {
			messages.add("* Length must be between " + minLength + " and " + maxLength + ".");
			messages.add("* Field must not be empty.");
			String messageTemplate = messages.stream().collect(Collectors.joining("\n"));

			context.buildConstraintViolationWithTemplate(messageTemplate).addConstraintViolation()
					.disableDefaultConstraintViolation();
			return textMinLength && textMaxLength && textNotEmpty;
		}
	}

}
