package com.feeedify.commands;

import lombok.Data;

@Data
public class FeedItemCommand {

    private String title;
    private String description;
    private String imageUrl;
    private String link;
    private String channelTitle;
    private String websiteLink;
    private String author;
    private String channelCategory;
    private Integer elapsedPublishMinutes;
    private Integer elapsedPublishHoures;
    private Long elapsedPublishDays;

}
