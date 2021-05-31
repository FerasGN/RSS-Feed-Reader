package de.htwsaar.pib2021.rss_feed_reader.converters;

import de.htwsaar.pib2021.rss_feed_reader.commands.UserCommand;
import de.htwsaar.pib2021.rss_feed_reader.commands.UserProfileUpdateCommand;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.User;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserProfileUpdateCommandToUser implements Converter<UserProfileUpdateCommand , User> {

    private BCryptPasswordEncoder passwordEncoder;

    public UserProfileUpdateCommandToUser(BCryptPasswordEncoder passwordEncoder){
        this.passwordEncoder = passwordEncoder;
    }
    @Nullable
    @Override
    public User convert(UserProfileUpdateCommand source) {
        if (source == null) {
            return null;
        }

        final User user = new User();
        user.setUsername(source.getUserName());
        user.setEmail(source.getEmail());
        user.setFirstName(source.getFirstName());
        user.setLastName(source.getLastName());
        user.setCountry(source.getCountry());
        user.setJob(source.getJob());
        user.setAge(source.getAge());
        user.setPassword(passwordEncoder.encode(source.getPassword()));

        return user;
    }
}
