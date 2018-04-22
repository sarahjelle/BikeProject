/*
 * Created by Fredrik Mediå
 */
package myapp.MailHandler;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

/**
 * MailHandler is the class taking care of and holding all the information needed to send a mail. This is used
 * by calling the constructor with the required parameters and the mail i sent.
 */
public class MailHandler {

    /**
     * The constructor does it all for you. In this project we normally just send one mail at the time. And the information is different
     * for each person if we are to send to more than one. Therefore we have chosen to use only the constructor. Then you wont be needing
     * to keep track fo the object after used it.
     *
     * @param subject   the title of the email
     * @param mailTo    the mail address of the receiver
     * @param text      the whole message which is sent to the receiver
     * @throws MessagingException
     */
    public MailHandler(String subject, String mailTo, String text) throws MessagingException {
        final String    mail        = "su1.team3@gmail.com",
                        password    = "vielskerjava",
                        host        = "smtp.gmail.com";

        Properties  props = System.getProperties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.user", mail);
        props.put("mail.smtp.password", password);
        props.put("mail.smtp.port", 587);
        props.put("mail.smtp.auth", "true");

        Session session = Session.getDefaultInstance(props, null);

        try {
            MimeMessage msg = new MimeMessage(session);

            msg.setFrom(new InternetAddress(mail));
            msg.setRecipients(Message.RecipientType.TO, mailTo);

            msg.setSubject(subject);

            msg.setSentDate(new Date());
            msg.setText(text);

            Transport transport = session.getTransport("smtp");
            transport.connect(host, mail, password);
            transport.sendMessage(msg, msg.getAllRecipients());
            transport.close();

        } catch(MessagingException ex) {
            ex.printStackTrace();
        }
    }
}

class MHTest {
    public static void main(String[] args) throws MessagingException {
        new MailHandler("New User", "fredrikkarst@gmail.com", "Dette er en testmelding");
    }
}
