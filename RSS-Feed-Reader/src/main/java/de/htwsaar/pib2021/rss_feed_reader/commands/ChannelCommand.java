package de.htwsaar.pib2021.rss_feed_reader.commands;



import lombok.Data;

@Data
public class ChannelCommand {

    private String title;

    private CategoryCommand categoryCommand = new CategoryCommand();

    private String url;

    private Long numberOfUnreadFeeds;
}
