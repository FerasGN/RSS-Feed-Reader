package com.feeedify.commands;

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
