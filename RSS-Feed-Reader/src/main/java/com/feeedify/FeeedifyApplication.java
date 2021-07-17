package com.feeedify;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.feeedify.database.MaterializedViewManager;

@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
public class FeeedifyApplication {

	@Autowired
	private MaterializedViewManager materializedViewManager;

	public static void main(String[] args) {
		SpringApplication.run(FeeedifyApplication.class, args);

	}

	@EventListener(ApplicationReadyEvent.class)
	public void init() throws InterruptedException {
		List<Long> l = materializedViewManager.fullTextSearchFeedItem("years");
		System.out.println("<-----------------------The ids -------------------->");
		System.out.println(l);
		System.out.println("End <-----------------------The ids-------------------->");
	}

}
