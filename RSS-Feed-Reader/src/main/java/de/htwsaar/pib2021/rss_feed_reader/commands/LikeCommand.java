package de.htwsaar.pib2021.rss_feed_reader.commands;

import lombok.Data;

@Data
public class LikeCommand {
    
    private Long feedId;
    private boolean liked;
}
