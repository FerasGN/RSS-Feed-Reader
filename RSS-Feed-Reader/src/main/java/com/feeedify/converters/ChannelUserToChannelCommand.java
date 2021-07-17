package com.feeedify.converters;


import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import com.feeedify.commands.CategoryCommand;
import com.feeedify.commands.ChannelCommand;
import com.feeedify.database.entity.ChannelUser;
import com.feeedify.rest.service.FeedsService;

@Component
public class ChannelUserToChannelCommand implements Converter<ChannelUser, ChannelCommand> {

    private FeedsService feedService;

    public ChannelUserToChannelCommand(FeedsService feedService) {
        this.feedService = feedService;
    }

    @Nullable
    @Override
    public ChannelCommand convert(ChannelUser source) {
        if (source == null) {
            return null;
        }

        final ChannelCommand channelCommand = new ChannelCommand();
        channelCommand.setTitle(source.getChannel().getTitle());
        channelCommand.setFaviconLink(source.getChannel().getFaviconLink());
        channelCommand.setChannelUrl(source.getChannel().getChannelUrl());
        CategoryCommand categoryCommand = new CategoryCommand();
        categoryCommand.setName(source.getCategory().getName());
        channelCommand.setCategoryCommand(categoryCommand);
        Long numberOfUnreadFeeds = feedService.findNumberOfUnreadFeedsOfChannel(source.getUser(),
                source.getChannel().getTitle());
        channelCommand.setNumberOfUnreadFeeds(numberOfUnreadFeeds);

        return channelCommand;
    }

}
