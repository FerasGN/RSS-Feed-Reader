package de.htwsaar.pib2021.rss_feed_reader.commands;

import javax.validation.constraints.Email;

import de.htwsaar.pib2021.rss_feed_reader.validators.annotation.ValidFormInput;
import de.htwsaar.pib2021.rss_feed_reader.validators.annotation.ValidPassword;
import lombok.Data;

@Data
public class UserCommand {

    @ValidFormInput(minLength = 3, maxLength = 60, blank = false)
    private String firstName;

    @ValidFormInput(minLength = 3, maxLength = 60, blank = false)
    private String lastName;

    @Email
    private String email;

    @ValidFormInput(minLength = 5, maxLength = 30, blank = false)
    private String username;

    @ValidPassword
    private String password;
}
