package de.htwsaar.pib2021.rss_feed_reader.database.entity;


import com.sun.xml.bind.v2.TODO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity(name = "Channel")
@Table(
        name = "channel",
        uniqueConstraints = {
                @UniqueConstraint(name = "unique_channel_name",
                        columnNames = "name"),
                @UniqueConstraint(name = "unique_url_channel",
                        columnNames = "url")
        }

)
public class Channel {

    @Id
    @SequenceGenerator(
            name = "channel_sequence",
            sequenceName = "channel_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "channel_sequence"
    )
    @Column(
            name = "id",
            updatable = false
    )

    private long id;
    @Column(name = "description",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String description;
    @Column(name = "name",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String name;
    @Column(name = "url",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String url;
    @Column(name = "category",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String category;
    //TODO image attribut

    @OneToMany(mappedBy = "Channel", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private Set<channel_subscriber> channel_subscriber;

    @OneToMany(mappedBy = "Channel", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private Set<FeedItem> feedItem;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category categorys;

    public Channel(long id, String description, String name, String url, String category) {
        this.id = id;
        this.description = description;
        this.name = name;
        this.url = url;
        this.category = category;
    }

    public Channel() {
    }
}