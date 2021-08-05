package com.feeedify.converters;

import java.time.Duration;
import java.time.LocalDateTime;


import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import com.feeedify.commands.FeedItemCommand;
import com.feeedify.database.entity.FeedItem;
import lombok.Data;

@Component
@Data
public class FeedItemToFeedItemCommand implements Converter<FeedItem, FeedItemCommand> {
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
        feedItemCommand.setImageUrl(source.getImageUrl());
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
