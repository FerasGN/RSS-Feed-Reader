package de.htwsaar.pib2021.rss_feed_reader.database.entity;

import lombok.Data;

import javax.persistence.*;

import de.htwsaar.pib2021.rss_feed_reader.database.entity.compositeIds.ChannelUserId;

@Data
@Entity
public class ChannelUser {

	@EmbeddedId
	private ChannelUserId id;
	private String category;
	private Boolean favorite;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("channelId")
	private Channel channel;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("userId")
	private User user;

}
