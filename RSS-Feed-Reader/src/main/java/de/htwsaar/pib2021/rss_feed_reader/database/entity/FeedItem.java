package de.htwsaar.pib2021.rss_feed_reader.database.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
@Entity(name = "Feed_Item")
@Table(name = "feed_item")
public class FeedItem {
    @Id
    @SequenceGenerator(
            name = "feed_item_sequence",
            sequenceName = "feed_item_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "feed_item_sequence"
    )
    @Column(
            name = "id",
            updatable = false
    )
    private long id;

    @Column(
            name = "content",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String content;
    @Column(
            name = "description",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String description;
    @Column(
            name = "link",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String link;
    @Column(
            name = "title",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String title;
    @Temporal(TemporalType.DATE)
    @Column(
            name = "publish_date",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private Date publish_date;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "channel_id", updatable = false, insertable = false)
    private Channel channel;

    @OneToMany(mappedBy = "feedItem", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private Set<Feed_item_x_subscriber> feed_item_x_subscriber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "feed_item_author_feed_item",
            joinColumns = {
                @JoinColumn(name = "feed_item_author_id", referencedColumnName = "id", nullable = false, updatable = false)
            },
            inverseJoinColumns = {
                @JoinColumn(name = "feed_item_id", referencedColumnName = "id", nullable = false, updatable = false)
            }

    )
    private Set<FeedItemAuthor> feedItemAuthors;


    public FeedItem(String content,
                    String description, String link, String title,
                    Date publish_date, Channel channel, Set<Feed_item_x_subscriber> feed_item_x_subscriber,
                    Category category) {

        this.content = content;
        this.description = description;
        this.link = link;
        this.title = title;
        this.publish_date = publish_date;
        this.channel = channel;
        this.feed_item_x_subscriber = feed_item_x_subscriber;
        this.category = category;
    }

    public FeedItem() {
    }

    @Override
    public String toString() {
        return "FeedItem{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", description='" + description + '\'' +
                ", link='" + link + '\'' +
                ", title='" + title + '\'' +
                ", publish_date=" + publish_date +
                '}';
    }
}
