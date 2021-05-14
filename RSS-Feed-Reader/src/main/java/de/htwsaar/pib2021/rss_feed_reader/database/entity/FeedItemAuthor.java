package de.htwsaar.pib2021.rss_feed_reader.database.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity(name = "Feed_Item_Author")
@Table(name = "feed_item_author")
public class FeedItemAuthor {
    
    @Id
    @SequenceGenerator(
            name = "feed_item_author_sequence",
            sequenceName = "feed_item_author_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "feed_item_author_sequence"
    )
    private long id;
    @Column(
            name = "name",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String name;

    @ManyToMany(mappedBy = "feed_item_author", fetch = FetchType.LAZY)
    private Set<FeedItem> feed_item;

    public FeedItemAuthor(FeedItem feedItem, String name) {
        this.feed_item.add(feedItem);
        this.name = name;
    }

    public FeedItemAuthor() {
    }

    @Override
    public String toString() {
        return "FeedItemAuthor{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
