package com.salesforce.git.management.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by rlamore on 1/1/15.
 */
public class EmailUtil {
    public static final String FROM = "rlamore@blitzraiden-inst2-1-sfm";
    public static final String[] RECIPIENTS = new String[] { "githubadmin@salesforce.com" };

    private static final Logger logger = LoggerFactory.getLogger(EmailUtil.class);

    private final String host;

    public EmailUtil(String host) {
        this.host = host;
    }

    public void sendEmail(String text) throws IOException {
        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.setProperty("mail.smtp.host", host);

        // Get the default Session object.
        Session session = Session.getDefaultInstance(properties);

        try {

            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(FROM));

            for (String recipient : RECIPIENTS) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            }

            // Set Subject: header field
            message.setSubject("GitHub Repository Request");

            // Now set the actual message
            message.setText(text);

            // Send message
            Transport.send(message);
            logger.info("Sent message successfully....");
        } catch (MessagingException e) {
            throw new RuntimeException("Cannot send mail", e);
        }
    }
}
