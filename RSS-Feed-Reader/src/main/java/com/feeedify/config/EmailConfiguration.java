package com.feeedify.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@ComponentScan(basePackages = { "com.feeedify.rest.service.email" })
@PropertySource(value = { "classpath:application.properties" })
@Configuration
public class EmailConfiguration {

	private String mailServerUsername;

	private String mailServerPassword;

	@Value("${spring.mail.host}")
	private String mailServerHost;

	@Value("${spring.mail.port}")
	private Integer mailServerPort;

	@Value("${spring.mail.properties.mail.smtp.auth}")
	private String mailServerAuth;

	@Value("${spring.mail.properties.mail.smtp.starttls.enable}")
	private String mailServerStartTls;

	@Autowired
	private Environment env;

	@Bean
	public JavaMailSender getJavaMailSender() {
		mailServerUsername = env.getProperty("EMAIL");
		mailServerPassword = env.getProperty("EMAIL_PASSWORD");
		
		if (mailServerUsername.equals("NOT_FOUND") || mailServerPassword.equals("NOT_FOUND"))
			return null;
		
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

		mailSender.setHost(mailServerHost);
		mailSender.setPort(mailServerPort);

		mailSender.setUsername(mailServerUsername);
		mailSender.setPassword(mailServerPassword);

		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", mailServerAuth);
		props.put("mail.smtp.starttls.enable", mailServerStartTls);
		
		mailSender.setJavaMailProperties(props);

		return mailSender;
	}

	@Bean
	public SimpleMailMessage simpleMailMessage() {
		SimpleMailMessage message = new SimpleMailMessage();
		return message;
	}

}