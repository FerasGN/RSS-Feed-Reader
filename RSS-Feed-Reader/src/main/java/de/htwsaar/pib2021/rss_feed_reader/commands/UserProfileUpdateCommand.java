package de.htwsaar.pib2021.rss_feed_reader.commands;

import de.htwsaar.pib2021.rss_feed_reader.validators.annotation.ValidFormInput;
import de.htwsaar.pib2021.rss_feed_reader.validators.annotation.ValidPassword;
import lombok.Data;

import javax.validation.constraints.Email;

@Data
public class UserProfileUpdateCommand {

    private long id;

    @ValidFormInput(minLength = 6, maxLength = 60, blank = false)
    private String firstName;

    @ValidFormInput(minLength = 6, maxLength = 60, blank = false)
    private String lastName;

    @Email
    private String email;

    private String country;

    private String job;

    private int age;
}
