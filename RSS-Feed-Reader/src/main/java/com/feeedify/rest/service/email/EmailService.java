package com.feeedify.rest.service.email;

import org.springframework.stereotype.Service;
import static com.feeedify.constants.Constants.*;

import java.util.Date;

import com.feeedify.database.entity.ConfirmationToken;
import com.feeedify.database.entity.User;
import com.feeedify.rest.service.AccountService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@Service
public class EmailService {
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Value("${EMAIL:NOT_FOUND}")
	private String fromEmail;

	@Autowired
	private ConfirmationTokenService confirmationTokenService;
	
	@Autowired
	private JavaMailSender emailSender;

	public boolean sendConfirmationEmail(User reciever) {

		if (reciever == null)
			return false;

		ConfirmationToken confirmationToken = new ConfirmationToken(reciever);

		String emailBody = CONFIRMATION_MESSAGE + confirmationToken.getConfirmationToken();

		sendEmail(reciever.getEmail(), "Complete Registration!", emailBody);
		confirmationToken.setSent(true);
		confirmationTokenService.save(confirmationToken);

		return true;

	}

	private boolean sendEmail(String to, String subject, String messageText) {
		boolean sent = false;
		if (fromEmail.equals("NOT_FOUND"))
			return sent;

		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom(fromEmail);
			message.setTo(to);
			message.setSubject(subject);
			message.setText(messageText);
			message.setSentDate(new Date());
			if (emailSender != null) {
				emailSender.send(message);
				sent = true;
			}

		} catch (MailException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}

		return sent;
	}

}
