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

			//create channel
			Channel channel = new Channel("Channel description", " channel name",
					"facebook.com", "politik");

			//create channel category
			Category category = new Category("politik");

			//create feeditem
			FeedItem feedItem = new FeedItem(channel.getId(), "feed content", "feed description",
					"feed link", "feed title", new Date(12385), channel,
					feed_item_x_subscriberSet, category);

			//create feedItemAuthor
			FeedItemAuthor feedItemAuthor = new FeedItemAuthor(feedItem.getId(), "Garri Zingraff");

			//create channel subscriber
			Channel_subscriber channel_subscriber = new Channel_subscriber(subscriber, channel);
			channel_subscriberSet.add(channel_subscriber);

			//create Feed_item_x_subscriber
			Feed_item_x_subscriber feed_item_x_subscriber = new Feed_item_x_subscriber(
					4, true, 2, true, feedItem, subscriber);
			feed_item_x_subscriberSet.add(feed_item_x_subscriber);





			//save all in repositories
			channelRepository.save(channel);
			feedItemRepository.save(feedItem);
			subscriberRepository.save(subscriber);
			categoryRepository.save(category);
			feedItemAuthorRepository.save(feedItemAuthor);
			feedItemXSubscriberRepository.save(feed_item_x_subscriber);
			channelSubscriberRepository.save(channel_subscriber);


		};
	}

}
