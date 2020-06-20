package cn.edu.hunnu.acm.serviceimpl;

import cn.edu.hunnu.acm.framework.annotation.Service;
import cn.edu.hunnu.acm.model.User;
import cn.edu.hunnu.acm.service.EmailService;
import org.apache.ibatis.io.Resources;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

@Service("emailService")
public class EmailServiceImpl implements EmailService {
    private Properties properties;
    private String USERNAME;
    private String PASSWORD;
    private String DEFAULTENCODING;
    private volatile Session session;

    public EmailServiceImpl() {
        properties = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = Resources.getResourceAsStream("mail.properties");
            properties.load(inputStream);
            USERNAME = properties.getProperty("mail.smtp.username");
            PASSWORD = properties.getProperty("mail.smtp.password");
            DEFAULTENCODING = properties.getProperty("mail.smtp.defaultEncoding");

            //session.setDebug(true);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Session getSession() {
        if(session == null) {
            synchronized (this) {
                if(session == null) {
                    session = Session.getDefaultInstance(properties, new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(USERNAME, PASSWORD);
                        }
                    });
                }
            }
        }
        return session;
    }

    @Override
    public void sendMailSimple(User user) {
        try {
            MimeMessage message = new MimeMessage(getSession());
            message.setFrom(new InternetAddress(USERNAME, "HUNNU OJ"));
            message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(user.getEmail(), user.getUserName()));
            message.setSubject(String.format("Hello %s, 你的密码已发送至邮箱, 请接收", user.getUserId()), DEFAULTENCODING);
            message.setText(String.format("您的密码是： %s!\n", user.getPassword()));
            message.setSentDate(new Date());

            Transport.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
