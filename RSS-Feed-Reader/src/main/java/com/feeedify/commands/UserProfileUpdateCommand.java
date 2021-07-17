package com.feeedify.commands;

import com.feeedify.validators.annotation.ValidFormInput;
import lombok.Data;

import javax.validation.constraints.Email;

@Data
public class UserProfileUpdateCommand {

    private Long id;

    @ValidFormInput(minLength = 6, maxLength = 60, blank = false, containsOnlyLettersAndNumbers = true)
    private String firstName;

    @ValidFormInput(minLength = 6, maxLength = 60, blank = false, containsOnlyLettersAndNumbers = true)
    private String lastName;

    @ValidFormInput(minLength = 5, maxLength = 30, blank = false, containsOnlyLettersAndNumbers = true)
    private String userName;

    @Email
    private String email;

    private String country;

    private String job;

    private Integer age;
}
