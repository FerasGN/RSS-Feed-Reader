package de.htwsaar.pib2021.rss_feed_reader.converters;

import de.htwsaar.pib2021.rss_feed_reader.commands.UserProfileUpdateCommand;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.User;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class UserToUserProfileUpdateCommand implements Converter<User, UserProfileUpdateCommand> {

    @Nullable
    @Override
    public UserProfileUpdateCommand convert(User source) {
        if (source == null) {
            return null;
        }

        final UserProfileUpdateCommand userProfileUpdateCommand = new UserProfileUpdateCommand();
        userProfileUpdateCommand.setEmail(source.getEmail());
        userProfileUpdateCommand.setFirstName(source.getFirstName());
        userProfileUpdateCommand.setLastName(source.getLastName());
        userProfileUpdateCommand.setCountry(source.getCountry());
        userProfileUpdateCommand.setJob(source.getJob());
        userProfileUpdateCommand.setAge(source.getAge());

        return userProfileUpdateCommand;
    }
}