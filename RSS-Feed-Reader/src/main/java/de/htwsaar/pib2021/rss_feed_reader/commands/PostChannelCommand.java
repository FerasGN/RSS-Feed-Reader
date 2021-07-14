package de.htwsaar.pib2021.rss_feed_reader.commands;

import lombok.Data;

@Data
public class PostChannelCommand {
    
    private String channelUrl;
    private String category;
    private String currentFeedsUrl;
    private String categoryUrl;
    private String channelTitle;
    private String selectedView;
    private String selectedPeriod;
    private String selectedOrder;

}
