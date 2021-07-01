package de.htwsaar.pib2021.rss_feed_reader.converters;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import de.htwsaar.pib2021.rss_feed_reader.commands.FeedItemCommand;
import de.htwsaar.pib2021.rss_feed_reader.commands.FeedItemUserCommand;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.FeedItem;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.FeedItemUser;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.User;
import lombok.Data;

@Component
@Data
public class FeedItemUserToFeedItemUserCommand implements Converter<FeedItemUser, FeedItemUserCommand> {
    private User user;
    private String channelCategory;

    @Nullable
    @Override
    public FeedItemUserCommand convert(FeedItemUser source) {
        if (source == null) {
            return null;
        }

        final FeedItemUserCommand feedItemUserCommand = new FeedItemUserCommand();
        FeedItemToFeedItemCommand feedItemToFeedItemCommand = new FeedItemToFeedItemCommand();
        FeedItem feedItem = source.getFeedItem();
        FeedItemCommand feedItemCommand = feedItemToFeedItemCommand.convert(feedItem);
        feedItemUserCommand.setUserId(user.getId());
        feedItemUserCommand.setFeedItemId(feedItem.getId());
        feedItemUserCommand.setFeedItemCommand(feedItemCommand);
        feedItemUserCommand.setLiked(source.isLiked());
        feedItemUserCommand.setReadLater(source.isReadLater());
        feedItemUserCommand.setRead(source.isRead());

        return feedItemUserCommand;
    }

}