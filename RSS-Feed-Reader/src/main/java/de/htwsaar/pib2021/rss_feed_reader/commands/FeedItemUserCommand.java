package de.htwsaar.pib2021.rss_feed_reader.commands;

import lombok.Data;

@Data
public class FeedItemUserCommand {

    private Long userId;
    private Long feedItemId;
    private FeedItemCommand feedItemCommand = new FeedItemCommand();
    private boolean liked;
    private boolean read;
    private boolean readLater;

}
