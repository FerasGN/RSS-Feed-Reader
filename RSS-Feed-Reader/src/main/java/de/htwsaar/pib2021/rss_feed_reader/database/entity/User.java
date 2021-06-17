package de.htwsaar.pib2021.rss_feed_reader.database.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

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
@Table(name = "`user`", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username", name = "uniqueUsernameConstraint"),
        @UniqueConstraint(columnNames = "email", name = "uniqueEmailConstraint")
})
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
    private Integer age;
    @Column(name = "enabled")
    private boolean enabled;
    @Column(name = "accountNonExpired")
    private boolean accountNonExpired;
    @Column(name = "accountNonLocked")
    private boolean accountNonLocked;
    @Column(name = "credentialsNonExpired")
    private boolean credentialsNonExpired;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_x_interest", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    @Column(name = "user_interest")
    private List<String> userInterests = new ArrayList<String>();

    @ToString.Exclude
    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChannelUser> channelUsers = new ArrayList<ChannelUser>();

    @ToString.Exclude
    @OneToMany(mappedBy = "feedItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeedItemUser> feedItemUsers = new ArrayList<FeedItemUser>();

}
