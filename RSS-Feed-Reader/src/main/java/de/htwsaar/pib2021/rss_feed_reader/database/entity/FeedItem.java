package de.htwsaar.pib2021.rss_feed_reader.database.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

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

    private long channel_id;
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

    public FeedItem(long id, long channel_id,
                    String content, String description,
                    String link, String title,
                    Date publish_date) {
        this.id = id;
        this.channel_id = channel_id;
        this.content = content;
        this.description = description;
        this.link = link;
        this.title = title;
        this.publish_date = publish_date;
    }

    public FeedItem() {
    }

    @Override
    public String toString() {
        return "FeedItem{" +
                "id=" + id +
                ", channel_id=" + channel_id +
                ", content='" + content + '\'' +
                ", description='" + description + '\'' +
                ", link='" + link + '\'' +
                ", title='" + title + '\'' +
                ", publish_date=" + publish_date +
                '}';
    }
}
