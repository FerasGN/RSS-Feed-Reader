package de.htwsaar.pib2021.rss_feed_reader.commands;

import lombok.Data;

@Data
public class ReadCommand {
    
    private Long feedId;
    private boolean read;
}
