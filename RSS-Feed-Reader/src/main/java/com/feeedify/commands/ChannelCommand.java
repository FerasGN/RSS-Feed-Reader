package com.feeedify.commands;

import lombok.Data;

@Data
public class ChannelCommand {

    private String title;
    private CategoryCommand categoryCommand = new CategoryCommand();
    private String channelUrl;
    private String faviconLink;
    private Long numberOfUnreadFeeds;
}
