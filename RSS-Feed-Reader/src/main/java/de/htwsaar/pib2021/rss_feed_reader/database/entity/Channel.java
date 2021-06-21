package de.htwsaar.pib2021.rss_feed_reader.database.entity;

import lombok.*;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "channel", uniqueConstraints = {
		@UniqueConstraint(name = "unique_url_channel", columnNames = "channel_url") })
public class Channel extends BaseEntity {

	@Column(name = "title", nullable = false, columnDefinition = "TEXT")
	private String title;
	@Column(name = "channel_url", nullable = false, columnDefinition = "TEXT")
	private String channelUrl;
	@Column(name = "website_link", nullable = true, columnDefinition = "TEXT")
	private String websiteLink;
	@Column(name = "favicon_link", nullable = true, columnDefinition = "TEXT")
	private String faviconLink;
	@Column(name = "description", nullable = true, columnDefinition = "TEXT")
	private String description;
	@Column(name = "language", nullable = true, columnDefinition = "TEXT")
	private String language;

	// @Lob
	// private Byte[] image;

	@ToString.Exclude
	@OneToMany(mappedBy = "channel", cascade = CascadeType.REMOVE)
	private List<FeedItem> feedItems = new ArrayList<FeedItem>();

	@ToString.Exclude
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ChannelUser> users = new ArrayList<ChannelUser>();

	public void addFeedItem(FeedItem feedItem) {
		feedItem.setChannel(this);
		this.feedItems.add(feedItem);
	}

}
