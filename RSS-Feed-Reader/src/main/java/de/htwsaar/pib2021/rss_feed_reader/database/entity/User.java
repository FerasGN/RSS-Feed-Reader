package de.htwsaar.pib2021.rss_feed_reader.database.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "`user`")
public class User extends BaseEntity {

    @Column(name = "first_name", nullable = false, columnDefinition = "TEXT")
    private String firstName;
    @Column(name = "last_name", nullable = false, columnDefinition = "TEXT")
    private String lastName;
    @Column(name = "email", nullable = false, columnDefinition = "TEXT")
    private String email;
    @Column(name = "username", nullable = false, columnDefinition = "TEXT")
    private String username;
    @Column(name = "password", columnDefinition = "TEXT")
    private String password;
    @Column(name = "country", columnDefinition = "TEXT")
    private String country;
    @Column(name = "job", columnDefinition = "TEXT")
    private String job;
    @Column(name = "age")
    private int age;
    @Column(name = "enabled")
    private boolean enabled;

    @ToString.Exclude
    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChannelUser> channels = new ArrayList<ChannelUser>();

    @ToString.Exclude
    @OneToMany(mappedBy = "feedItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeedItemUser> feedItems = new ArrayList<FeedItemUser>();

}
