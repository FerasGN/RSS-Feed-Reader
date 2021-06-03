package de.htwsaar.pib2021.rss_feed_reader.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;

import de.htwsaar.pib2021.rss_feed_reader.commands.ChannelCommand;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.ChannelUser;
import de.htwsaar.pib2021.rss_feed_reader.rest.service.ChannelService;

public class ChannelUserToChannelCommand implements Converter<ChannelUser, ChannelCommand> {

    private ChannelService channelService;

    public ChannelUserToChannelCommand(ChannelService channelService){
        this.channelService = channelService;      
    }

    @Nullable
    @Override
    public ChannelCommand convert(ChannelUser source) {
        if (source == null) {
            return null;
        }

        final ChannelCommand channelCommand = new ChannelCommand();
        channelCommand.setName(source.getChannel().getName());
        channelCommand.setCategory(source.getCategory());
        Long numberOfUnreadFeeds = channelService.findNumberOfUnreadFeeds(source.getUser(), source.getChannel());
        channelCommand.setNumberOfUnreadFeeds(numberOfUnreadFeeds);

        return channelCommand;
    }

}
