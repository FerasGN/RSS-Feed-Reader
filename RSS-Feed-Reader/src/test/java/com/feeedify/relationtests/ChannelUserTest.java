package com.feeedify.relationtests;

import com.feeedify.database.entity.Category;
import com.feeedify.database.entity.Channel;
import com.feeedify.database.entity.ChannelUser;
import com.feeedify.database.entity.User;
import com.feeedify.database.entity.compositeIds.ChannelUserId;
import com.feeedify.database.repository.CategoryRepository;
import com.feeedify.database.repository.ChannelRepository;
import com.feeedify.database.repository.ChannelUserRepository;
import com.feeedify.database.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.transaction.BeforeTransaction;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


@DataJpaTest
public class ChannelUserTest {

    @Autowired
    private ChannelRepository channelRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private CategoryRepository categoryRepo;
    @Autowired
    private ChannelUserRepository channelUserRepo;

    private static final String DESCRIPTION = "Google-feed channel description";
    private static final String URL = "https://news.google.com";
    private static final String NAME = "Google-feed";

    private static final String DESCRIPTION_ = "A short description for channel2";
    private static final String URL_= "https://news.com";
    private static final String NAME_ = "Feedify-feed";

    @BeforeTransaction
    public void init(){
        Channel channel = new Channel();
        channel.setId(1l);
        channel.setDescription(DESCRIPTION_);
        channel.setChannelUrl(URL);
        channel.setTitle(NAME);
        channel = channelRepo.save(channel);

        Channel channel_ = new Channel();
        channel_.setId(2l);
        channel_.setDescription(DESCRIPTION_);
        channel_.setChannelUrl(URL_);
        channel_.setTitle(NAME_);
        channel_ = channelRepo.save(channel_);


        User user = new User();
        user.setId(1l);
        user.setFirstName("jon");
        user.setLastName("snow");
        user.setUsername("jsnow");
        user.setEmail("jsnow@gmail.com");
        user.setAge(20);
        user.setPassword("123");
        userRepo.save(user);

        Category category = new Category();
        category.setId(1l);
        category.setName("Fiction");
        categoryRepo.save(category);

        ChannelUser channelUser = new ChannelUser();
        channelUser.setUser(user);
        channelUser.setChannel(channel);
        channelUser.setFavorite(true);
        channelUser.setCategory(new Category());
        channelUserRepo.save(channelUser);
    }

    @Test
    public void findChannelTest() {
        Channel channel = channelRepo.findById(1l).get();
        channel = channelRepo.save(channel);
        assertEquals(channel.getId(), 1l);
    }

    @Test
    public void FindUserIdInChannelUserIdTest(){
        ChannelUserId channelUserId = new ChannelUserId(1l,1l);
        ChannelUser channelUser = channelUserRepo.findById(channelUserId).get();
        long userId = channelUser.getId().getUserId();
        assertEquals(userId, 1l);
    }


    @Test
    public void FindChannelIdInChannelUserIdTest(){
        ChannelUserId channelUserId = new ChannelUserId(1l,1l);
        ChannelUser channelUser = channelUserRepo.findById(channelUserId).get();
        long channelId = channelUser.getId().getChannelId();
        assertEquals(channelId, 1l);
    }

    @Test
    public void FindUserNameInChannelUserTest(){
        ChannelUserId channelUserId = new ChannelUserId(1l,1l);
        ChannelUser channelUser = channelUserRepo.findById(channelUserId).get();
        long userId = channelUser.getId().getUserId();
        String userName = userRepo.findById(userId).get().getUsername();
        assertEquals(userName, "jsnow");
    }

    @Test
    public void deleteChannelUserTest(){
        ChannelUserId channelUserId = new ChannelUserId(1l,1l);
        List<ChannelUser> channelUserList = channelUserRepo.findAll();
        ChannelUser channelUser = channelUserRepo.findById(channelUserId).get();
        channelUserRepo.delete(channelUser);
        assertEquals(channelUserList.size(), 1);
    }

    @Test
    public void CheckUserPresenceAfterDeletingChannelUserTest(){
        User user = userRepo.findByUsername("jsnow").get();
        assertNotEquals(user, null);
    }


}
