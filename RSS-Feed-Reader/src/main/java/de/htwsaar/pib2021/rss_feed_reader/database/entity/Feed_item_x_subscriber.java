package de.htwsaar.pib2021.rss_feed_reader.database.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity(name = "Feed_item_x_subscriber")
@Table(
        name = "Feed_item_x_subscriber"
)
public class Feed_item_x_subscriber {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int Id;
    private float rating;
    private boolean read;
    private int clicks;
    private boolean important_item;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subscriber_id", nullable = false)
    private Subscriber subscriber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "feed_item_id", nullable = false)
    private FeedItem feedItem;

    public Feed_item_x_subscriber(){
    }

    public Feed_item_x_subscriber(float rating, boolean read, int clicks, boolean important_item, FeedItem feedItem, Subscriber subscriber){
        this.rating = rating;
        this.read = read;
        this.clicks = clicks;
        this.important_item = important_item;
        this.feedItem = feedItem;
        this.subscriber = subscriber;
    }
}
