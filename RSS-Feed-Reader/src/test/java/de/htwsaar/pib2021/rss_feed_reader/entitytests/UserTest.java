package de.htwsaar.pib2021.rss_feed_reader.entitytests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import de.htwsaar.pib2021.rss_feed_reader.database.entity.User;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.UserRepository;
import org.springframework.test.context.transaction.BeforeTransaction;

@DataJpaTest
class UserTest {

	@Autowired
	private UserRepository userRepo;

	@BeforeTransaction
	public void init(){
		User user = new User();
		user.setId(1l);
		user.setFirstName("jon");
		user.setLastName("snow");
		user.setUsername("jsnow");
		user.setEmail("jsnow@gmail.com");
		user.setAge(20);
		user.setPassword("123");
		userRepo.save(user);
	}

	@Test
	public void findUserTest() {
		User user = userRepo.findById(1l).get();
		assertNotEquals(user, null);
	}

	@Test
	public void updateUserTest() {
		User user = userRepo.findById(1l).get();
		user.setUsername("snowj");
		user = userRepo.save(user);

		assertEquals(user.getUsername(), "snowj");
	}

	@Test
	public void findAllUsersTest() {
		List<User> users = userRepo.findAll();
		assertSame(users.size(), 1);
	}

}
