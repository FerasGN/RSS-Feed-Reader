package de.htwsaar.pib2021.rss_feed_reader.commands;

import lombok.Data;

@Data
public class FeedItemCommand {

    private String title;
    private String description;
    private String link;
    private String channel;
    private String category;
    private Integer elapsedPublishMinutes;
    private Integer elapsedPublishHoures;
    private Integer elapsedPublishDays;

}
