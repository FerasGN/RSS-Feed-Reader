package de.htwsaar.pib2021.rss_feed_reader.database.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity(name = "channel_subscriber")
@Table(
        name = "channel_subscriber"
)
public class Channel_subscriber {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int         id;

    private boolean important_channel;

    /**@Column(name = "category",
            //columnDefinition = "TEXT"
    )
    private String  category;
    **/

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Subscriber  subscriber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Channel     channel;

    public Channel_subscriber(Subscriber subscriber, Channel channel){
        this.channel = channel;
        this.subscriber = subscriber;
        important_channel= false;
    }

    public Channel_subscriber(){}
}
