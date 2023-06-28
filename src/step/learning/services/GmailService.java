package step.learning.services;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class GmailService implements EmailService {

    private Session getEmailSession(){
        Properties emailProperties = new Properties();
        emailProperties.put("mail.smtp.auth", "true");
        emailProperties.put("mail.smtp.starttls.enable", "true");
        emailProperties.put("mail.smtp.port", "587");
        emailProperties.put("mail.smtp.trust", "smtp.gmail.com");
        emailProperties.put("mail.smtp.ssl.auth", "true");
        emailProperties.put("mail.smtp.ssl.protocols", "TLSv1.2");

        Session emailSession = Session.getInstance(emailProperties);
        emailSession.setDebug(true);

        return emailSession;
    }

    @Override
    public boolean send(String to, String subject, String text) {

        Session emailSession = getEmailSession();
        Transport emailTransport;
        try{
            emailTransport = emailSession.getTransport("smtp");
            emailTransport.connect("smtp.gmail.com", "drdrake1337@gmail.com", "vayqqflegkubfics");

            MimeMessage message = new MimeMessage(emailSession);
            message.setFrom(new InternetAddress("drdrake1337@gmail.com"));
            message.setSubject(subject);
            message.setContent(text, "text/html; charset=utf-8");

            emailTransport.sendMessage(message, InternetAddress.parse(to));
            emailTransport.close();


        } catch (MessagingException ex){
            System.out.println(ex.getMessage());
            return false;
        }
        return true;
    }
}
