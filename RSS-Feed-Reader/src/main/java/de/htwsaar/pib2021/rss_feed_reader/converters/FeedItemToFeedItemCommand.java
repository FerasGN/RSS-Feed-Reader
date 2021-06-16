package de.htwsaar.pib2021.rss_feed_reader.converters;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import de.htwsaar.pib2021.rss_feed_reader.commands.FeedItemCommand;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.Category;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.FeedItem;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.User;
import de.htwsaar.pib2021.rss_feed_reader.rest.service.ChannelService;
import lombok.Data;

@Component
@Data
public class FeedItemToFeedItemCommand implements Converter<FeedItem, FeedItemCommand> {
    private User user;
    private String channelCategory;

    @Nullable
    @Override
    public FeedItemCommand convert(FeedItem source) {
        if (source == null) {
            return null;
        }

        final FeedItemCommand feedItemCommand = new FeedItemCommand();
        feedItemCommand.setTitle(source.getTitle());
        feedItemCommand.setDescription(source.getDescription());
        feedItemCommand.setLink(source.getLink());
        feedItemCommand.setChannelTitle(source.getChannel().getTitle());
        feedItemCommand.setWebsiteLink(source.getChannel().getWebsiteLink());
        feedItemCommand.setAuthor(source.getAuthor());
        feedItemCommand.setChannelCategory(channelCategory);

        if (source.getPublishDate() != null) {
            feedItemCommand.setElapsedPublishMinutes(calculateElapsedPublishMinutes(source.getPublishDate()));
            feedItemCommand.setElapsedPublishHoures(calculateElapsedPublishHoures(source.getPublishDate()));
            feedItemCommand.setElapsedPublishDays(calculateElapsedPublishDay(source.getPublishDate()));
        }

        return feedItemCommand;
    }

    public Integer calculateElapsedPublishMinutes(LocalDateTime publishDate) {
        Duration duration = Duration.between(publishDate, LocalDateTime.now());
        return duration.toMinutesPart();
    }

    public Integer calculateElapsedPublishHoures(LocalDateTime publishDate) {
        Duration duration = Duration.between(publishDate, LocalDateTime.now());
        return duration.toHoursPart();
    }

    public Long calculateElapsedPublishDay(LocalDateTime publishDate) {
        Duration duration = Duration.between(publishDate, LocalDateTime.now());
        return duration.toDaysPart();
    }

}