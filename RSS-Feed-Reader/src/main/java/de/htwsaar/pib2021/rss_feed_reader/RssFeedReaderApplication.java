package de.htwsaar.pib2021.rss_feed_reader;



import de.htwsaar.pib2021.rss_feed_reader.database.entity.*;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class RssFeedReaderApplication {

	public static void main(String[] args) {
		SpringApplication.run(RssFeedReaderApplication.class, args);

	}
	@Bean
	public CommandLineRunner mappingDemo(ChannelRepository channelRepository,
										 FeedItemRepository feedItemRepository,
										 CategoryRepository categoryRepository,
										 FeedItemAuthorRepository feedItemAuthorRepository,
										 SubscriberRepository subscriberRepository,
										 Feed_item_x_subscriberRepository feedItemXSubscriberRepository,
										 Channel_subscriberRepository channelSubscriberRepository){
		return args -> {
			Set<Feed_item_x_subscriber> feed_item_x_subscriberSet = new HashSet<>();
			Set<Channel_subscriber> channel_subscriberSet = new HashSet<>();


			//create subscriber
			Subscriber subscriber = new Subscriber("rochella", "vofo", "rochelvofo@yahoo.com", "1254",
					"germany", "student", 21, channel_subscriberSet, feed_item_x_subscriberSet);

			//create channel category
			Category category = new Category("politik");

			//create channel
			Channel channel = new Channel("Channel description", " channel name",
					"facebook.com", category);
			Channel channel2 = new Channel("Channel description", " channel name2",
					"vinoleed.com", category);


			//create feeditem
			FeedItem feedItem = new FeedItem( "feed content", "feed description",
					"feed link", "feed title", new Date(12385), channel,
					feed_item_x_subscriberSet, category);

			//create feedItemAuthor
			FeedItemAuthor feedItemAuthor = new FeedItemAuthor(feedItem.getId(), "Garri Zingraff");

			//create channel subscriber
			Channel_subscriber channel_subscriber = new Channel_subscriber(
					true, subscriber, channel
			);
			Channel_subscriber channel_subscriber2 = new Channel_subscriber(
					true, subscriber, channel2
			);
			channel_subscriberSet.add(channel_subscriber);
			channel_subscriberSet.add(channel_subscriber2);

			//create Feed_item_x_subscriber
			Feed_item_x_subscriber feed_item_x_subscriber = new Feed_item_x_subscriber(
					4, true, 2, true, feedItem, subscriber);
			feed_item_x_subscriberSet.add(feed_item_x_subscriber);






			//save all in repositories
			feedItemXSubscriberRepository.save(feed_item_x_subscriber);
			channelRepository.save(channel);
			feedItemRepository.save(feedItem);
			subscriberRepository.save(subscriber);
			categoryRepository.save(category);
			feedItemAuthorRepository.save(feedItemAuthor);
			channelSubscriberRepository.save(channel_subscriber);


		};
	}

}
