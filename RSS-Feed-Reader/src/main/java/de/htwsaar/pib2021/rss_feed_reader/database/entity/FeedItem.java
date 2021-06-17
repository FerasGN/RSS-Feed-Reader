package de.htwsaar.pib2021.rss_feed_reader.database.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
public class FeedItem extends BaseEntity {

	@Column(name = "title", nullable = false, columnDefinition = "TEXT")
	private String title;
	@Column(name = "link", nullable = false, columnDefinition = "TEXT")
	private String link;
	@Column(name = "description", nullable = true, columnDefinition = "TEXT")
	private String description;
	@Column(name = "content", nullable = true, columnDefinition = "TEXT")
	private String content;
	@Column(name = "author", nullable = true, columnDefinition = "TEXT")
	private String author;
	@Column(name = "publish_date", nullable = false)
	private LocalDateTime publishDate;
	@Column(name = "publish_local_date", nullable = true)
	private LocalDate publishLocalDate;

	@ToString.Exclude
	@ManyToOne
	@JoinColumn(name = "channel_id")
	private Channel channel;

	@ToString.Exclude
	@ManyToMany(mappedBy = "feedItems")
	private List<Category> categories = new ArrayList<Category>();

	@ToString.Exclude
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<FeedItemUser> users = new ArrayList<FeedItemUser>();

	public void setCategories(List<Category> categories) {
		for (Category category : categories)
			addCategory(category);
	}

	public void addCategory(Category category) {
		this.categories.add(category);
		category.getFeedItems().add(this);

	}

	public void add(Channel channel) {
		this.setChannel(channel);
		channel.getFeedItems().add(this);

	}
}
