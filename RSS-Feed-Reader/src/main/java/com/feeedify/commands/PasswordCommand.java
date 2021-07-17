package com.feeedify.commands;

import com.feeedify.validators.annotation.ValidPassword;
import lombok.Data;

@Data
public class PasswordCommand {

    private String username;

    @ValidPassword
    private String password;
}
