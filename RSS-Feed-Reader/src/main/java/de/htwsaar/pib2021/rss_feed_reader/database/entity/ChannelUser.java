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
	@ToString.Exclude
	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category category;
	private boolean favorite;

	@MapsId("channelId")
	@JoinColumn(name = "channelId", referencedColumnName = "id", insertable = false, updatable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	private Channel channel;

	@MapsId("userId")
	@JoinColumn(name = "userId", referencedColumnName = "id", insertable = false, updatable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;

}
