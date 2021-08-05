package com.feeedify.rest.service.email;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.feeedify.database.entity.ConfirmationToken;
import com.feeedify.database.entity.User;
import com.feeedify.database.repository.ConfirmationTokenRepository;
import com.feeedify.rest.service.AccountService;

@Service
public class ConfirmationTokenService {

	@Autowired
	private ConfirmationTokenRepository confirmationTokenRepository;

	@Autowired
	private AccountService accountService;

	public ConfirmationToken findByConfirmationToken(String confirmationToken) {
		return confirmationTokenRepository.findByConfirmationToken(confirmationToken);
	}

	@Transactional
	public ConfirmationToken save(ConfirmationToken confirmationToken) {
		return confirmationTokenRepository.save(confirmationToken);
	}

	@Transactional
	public void removeUserOfConfirmationToken(Long userId) {
		confirmationTokenRepository.removeUserOfConfirmationToken(userId);
	}

	public void deleteByUserId(Long userId) {
		confirmationTokenRepository.deleteByUser_Id(userId);
	}

	/**
	 *
	 *
	 * @param token
	 *
	 * @author Feras Ejneid
	 */
	public void updateConfirmationToken(ConfirmationToken token) {
		Optional<User> optionalUser = accountService.findUserByEmail(token.getUser().getEmail());

		if (!optionalUser.isPresent())
			return;

		User user = optionalUser.get();
		user.setEnabled(true);
		accountService.saveUser(user);
		token.setUsed(true);
		save(token);
	}

}