package com.feeedify.database.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.feeedify.database.entity.ConfirmationToken;
import com.feeedify.database.entity.User;

@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {
	
	ConfirmationToken findByConfirmationToken(String confirmationToken);

	@Modifying
	@Query("update ConfirmationToken ct set ct.user = null where ct.user.id = ?1")
	@Transactional
	void removeUserOfConfirmationToken(Long userId);

	 void deleteByUser(User user);
}