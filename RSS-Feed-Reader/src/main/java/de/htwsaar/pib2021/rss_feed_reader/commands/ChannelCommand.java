package de.htwsaar.pib2021.rss_feed_reader.commands;


import lombok.Data;

@Data
public class ChannelCommand {

    private String name;

    private String category;

    private String url;

    private Long numberOfUnreadFeeds;
}
