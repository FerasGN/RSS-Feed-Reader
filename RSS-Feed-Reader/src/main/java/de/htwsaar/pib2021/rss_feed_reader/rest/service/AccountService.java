package de.htwsaar.pib2021.rss_feed_reader.rest.service;

import de.htwsaar.pib2021.rss_feed_reader.commands.UserProfileUpdateCommand;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.User;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    @Autowired
    UserRepository userRepository;

    public void updateProfileInfo(UserProfileUpdateCommand user){
        try {
            User userChanged = userRepository.findById(user.getId()).get();
            userChanged.setAge(user.getAge());
            userChanged.setCountry(user.getCountry());
            userChanged.setEmail(user.getEmail());
            userChanged.setFirstName(user.getFirstName());
            userChanged.setLastName(user.getLastName());
            userChanged.setJob(user.getJob());
            userRepository.save(userChanged);
        }catch (Exception e){

        }
    }

    public void changePassword(User user,String pw){
        try {
            User userChanged = userRepository.findById(user.getId()).get();
            userChanged.setPassword(pw);
            userRepository.save(userChanged);
        }catch (Exception e){

        }
    }

}
