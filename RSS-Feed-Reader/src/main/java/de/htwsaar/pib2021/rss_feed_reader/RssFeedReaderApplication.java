package de.htwsaar.pib2021.rss_feed_reader;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import de.htwsaar.pib2021.rss_feed_reader.database.MaterializedViewManager;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.FeedItemUser;
import de.htwsaar.pib2021.rss_feed_reader.rest.service.search.FeedSearchingService;

@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
public class RssFeedReaderApplication {

	@Autowired
	private MaterializedViewManager materializedViewManager;
	@Autowired
	private FeedSearchingService searchingService;

	public static void main(String[] args) {
		SpringApplication.run(RssFeedReaderApplication.class, args);

	}

	@EventListener(ApplicationReadyEvent.class)
	public void init() throws InterruptedException {

		// materializedViewManager.createMaterializedViewChannelWithIndex();
		// materializedViewManager.refreshFeedItem();

		List<Long> l = materializedViewManager.fullTextSeachrFeedItem("years", "ENGLISH");
		System.out.println("<-----------------------The ids -------------------->");
		System.out.println(l);
		// List<FeedItemUser> feeds = searchingService.searchAll(new User(), "years",
		// 0);
		// feeds.forEach(f -> {
		// System.out.println(f.getFeedItem().getId());
		// });
		System.out.println("End <-----------------------The ids-------------------->");
	}

}
