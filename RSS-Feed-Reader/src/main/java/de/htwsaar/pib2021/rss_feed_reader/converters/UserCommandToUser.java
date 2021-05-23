package de.htwsaar.pib2021.rss_feed_reader.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import de.htwsaar.pib2021.rss_feed_reader.commands.UserCommand;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.User;

@Component
public class UserCommandToUser implements Converter<UserCommand, User> {

    private BCryptPasswordEncoder passwordEncoder;

    public UserCommandToUser(BCryptPasswordEncoder passwordEncoder){
        this.passwordEncoder = passwordEncoder;
    }
    @Nullable
    @Override
    public User convert(UserCommand source) {
        if (source == null) {
            return null;
        }

        final User user = new User();
        user.setUsername(source.getUsername());
        user.setEmail(source.getEmail());
        user.setFirstName(source.getFirstName());
        user.setLastName(source.getLastName());
        user.setPassword(passwordEncoder.encode(source.getPassword()));

        return user;
    }

}
