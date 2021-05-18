package de.htwsaar.pib2021.rss_feed_reader.database.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

import java.time.ZonedDateTime;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
public class FeedItem extends BaseEntity {

	@Column(name = "title", nullable = false, columnDefinition = "TEXT")
	private String title;
	@Column(name = "link", nullable = false, columnDefinition = "TEXT")
	private String link;
	@Column(name = "description", nullable = false, columnDefinition = "TEXT")
	private String description;
	@Column(name = "content", nullable = false, columnDefinition = "TEXT")
	private String content;
	@Column(name = "publish_date", nullable = false)
	private ZonedDateTime publishDate;

	@ElementCollection
	@CollectionTable(name = "feedItem_x_authorName", joinColumns = @JoinColumn(name = "feed_item_id", referencedColumnName = "id"))
	@Column(name = "author_name")
	private Set<String> authorsNames;

	@ManyToOne
	@JoinColumn(name = "channel_id")
	private Channel channel;
	@ManyToMany(mappedBy = "feedItems")
	private Set<Category> categories;
}
