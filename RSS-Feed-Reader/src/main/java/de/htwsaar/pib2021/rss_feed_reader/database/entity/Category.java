package de.htwsaar.pib2021.rss_feed_reader.database.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "category", uniqueConstraints = { @UniqueConstraint(name = "unique_category_name", columnNames = "name") }

)
public class Category extends BaseEntity {

        private String name;

        @ToString.Exclude
        @ManyToMany
        @JoinTable(name = "feedItem_x_category", joinColumns = {
                        @JoinColumn(name = "category_id") }, inverseJoinColumns = { @JoinColumn(name = "feedItem_id") })
        private List<FeedItem> feedItems = new ArrayList<FeedItem>();

        @ToString.Exclude
        @OneToMany(mappedBy = "category", cascade = CascadeType.REMOVE)
        private List<ChannelUser> channelUsers = new ArrayList<ChannelUser>();

        public Category(String name) {
                this.name = name;
        }

        public void addChannelUser(ChannelUser cu) {
                cu.setCategory(this);
                this.channelUsers.add(cu);
        }

        public void addFeedItem(FeedItem feedItem) {
                feedItem.addCategory(this);
                this.feedItems.add(feedItem);
        }

}
