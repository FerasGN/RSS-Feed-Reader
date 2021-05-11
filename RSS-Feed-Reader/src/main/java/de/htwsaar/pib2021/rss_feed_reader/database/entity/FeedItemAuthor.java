package de.htwsaar.pib2021.rss_feed_reader.database.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

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
    //TODO Many to One oder One to One
    private long feed_item_id;

    public FeedItemAuthor(long feed_item_id, String name) {
        this.feed_item_id = feed_item_id;
        this.name = name;
    }

    public FeedItemAuthor() {
    }

    @Override
    public String toString() {
        return "FeedItemAuthor{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", feed_item_id=" + feed_item_id +
                '}';
    }
}
