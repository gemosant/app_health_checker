package app_health_checker;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailService {
  private Session session;
  private String account, mailAddresses;

  public MailService(String account, String password, String server, String port, String mailAddresses) {
    this.account = account;
    this.mailAddresses = mailAddresses;
    try {
      Properties props = new Properties();
      props.put("mail.smtp.host", server);
      props.put("mail.smtp.socketFactory.port", port);
      props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
      props.put("mail.smtp.auth", "true");
      props.put("mail.smtp.port", port);
      props.put("mail.smtp.starttls.enable", "true");
      props.put("mail.smtp.ssl.trust", "*");

      this.session = Session.getInstance(props, new javax.mail.Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
          return new PasswordAuthentication(account, password);
        }
      });
    } catch (Exception e) {
      LogWriter.error(e.getMessage());
    }
  }

  public void send(String messageBody, String subject) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        if (session == null) {
          LogWriter.error("Authentication failed while sending mail : " + account);
          return;
        }
        try {
          Message message = new MimeMessage(session);
          message.setFrom(new InternetAddress(account));
          message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mailAddresses));
          message.setSubject(subject);
          message.setContent(messageBody, "text/plain; charset=UTF-8");

          Transport transport = session.getTransport("smtps");
          Transport.send(message);
          transport.close();

        } catch (Exception e) {
          LogWriter.error(e.getMessage());
        }
      }
    }).start();
  }
}
