package myapp.MailHandler;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

public class MailHandler {
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
