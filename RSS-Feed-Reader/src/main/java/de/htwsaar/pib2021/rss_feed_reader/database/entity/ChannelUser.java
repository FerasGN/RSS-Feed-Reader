package de.htwsaar.pib2021.rss_feed_reader.database.entity;

import lombok.*;

import javax.persistence.*;

import de.htwsaar.pib2021.rss_feed_reader.database.entity.compositeIds.ChannelUserId;

@Data
@Entity
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ChannelUser {

	@EmbeddedId
	private ChannelUserId id = new ChannelUserId();
	private String category;
	private boolean favorite;

	@MapsId("channelId")
	@ManyToOne(fetch = FetchType.LAZY)
	private Channel channel;

	@MapsId("userId")
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;

}
