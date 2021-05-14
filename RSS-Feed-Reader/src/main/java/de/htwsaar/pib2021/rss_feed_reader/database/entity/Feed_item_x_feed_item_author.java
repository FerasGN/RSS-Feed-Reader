package de.htwsaar.pib2021.rss_feed_reader.database.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

//wo für ist diese Klasse?
//Lucas
@Getter
@Setter
@Entity
@Table
public class Feed_item_x_feed_item_author {

    @Id
    private long id;


}
