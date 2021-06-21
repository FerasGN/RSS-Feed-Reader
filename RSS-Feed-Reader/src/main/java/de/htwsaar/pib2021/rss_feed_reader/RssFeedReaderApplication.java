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

@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
public class RssFeedReaderApplication {

	@Autowired
	private MaterializedViewManager materializedViewManager;

	public static void main(String[] args) {
		SpringApplication.run(RssFeedReaderApplication.class, args);

	}

	@EventListener(ApplicationReadyEvent.class)
	public void init() throws InterruptedException {

		materializedViewManager.createMaterializedViewChannelWithIndex();
		materializedViewManager.creatematerializedViewFeedItemWithIndex();

		// List<Long> l = materializedViewManager.fullTextSeachrFeedItem(");
		// System.out.println("<-----------------------The ids -------------------->");
		// System.out.println(l);
		// System.out.println("End <-----------------------The
		// ids-------------------->");
	}

}
