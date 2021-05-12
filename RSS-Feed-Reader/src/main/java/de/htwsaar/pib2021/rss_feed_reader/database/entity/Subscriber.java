package de.htwsaar.pib2021.rss_feed_reader.database.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity(name = "Subscriber")
@Table(
        name = "subscriber",
        uniqueConstraints = {
                @UniqueConstraint(name = "subscriber_email_unique",
                                 columnNames = "email")
        }

)
public class Subscriber {
    @Id
    @SequenceGenerator(
            name = "subscriber_sequence",
            sequenceName = "subscriber_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "subscriber_sequence"
    )
    @Column(
            name = "id",
            updatable = false
    )
    private long id;
    @Column(name = "first_name",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String firstName;
    @Column(
            name = "last_name",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String lastName;
    @Column(
            name = "email",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String email;
    @Column(
            name = "password",
            columnDefinition = "TEXT"
    )
    private String password;
    @Column(
            name = "country",
            columnDefinition = "TEXT"
    )
    private String country;
    @Column(
            name = "job",
            columnDefinition = "TEXT"
    )
    private  String job;
    @Column(
            name = "age",
            nullable = false
    )
    private int age;

    @OneToMany(mappedBy = "Subscriber", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private Set<channel_subscriber> channel_subscriberSet;

    @OneToMany(mappedBy = "Subscriber", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private Set<Feed_item_x_subscriber> feed_item_x_subscribers;

    public Subscriber(long id, String firstName, String lastName,
                      String email, String password, String country,
                      String job, int age) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.country = country;
        this.job = job;
        this.age = age;
    }

    public Subscriber() {
    }

    @Override
    public String toString() {
        return "Subscriber{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", country='" + country + '\'' +
                ", job='" + job + '\'' +
                ", age=" + age +
                '}';
    }
}