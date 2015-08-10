package teamdivider.mail;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.stereotype.Service;

import teamdivider.util.PropertyUtil;

@Service
public class MailService {

  private boolean skip(MailInfo emailInfo) {
    if (PropertyUtil.emailOnlyToZonghan()) {
      for (String address : emailInfo.getAddresses()) {
        if (address.contains("sap")) {
          return true;
        }
      }
    }
    return false;
  }

  public void sendHtmlMail(MailInfo emailInfo) throws Exception {
    if (skip(emailInfo)) {
      return;
    }
    Properties props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.host", "smtp.sina.cn");

    String fromAddress = EmailAccountUtil.getAddress();
    if (fromAddress == null)
      throw new Exception("Email address used out!");
    String fromPwd = EmailAccountUtil.getPassword(fromAddress);

    MyAuthenticator authenticator = new MyAuthenticator(fromAddress, fromPwd);
    // get email session
    Session session = Session.getInstance(props, authenticator);

    // define MIME email object
    MimeMessage mimeMessage = new MimeMessage(session);

    mimeMessage.setFrom(new InternetAddress(fromAddress, "SF Sports Club"));

    mimeMessage.setRecipient(Message.RecipientType.TO, emailInfo.getEmailTo());
    mimeMessage.setRecipients(Message.RecipientType.CC, emailInfo.getEmailCc());
    mimeMessage.setSubject(emailInfo.getEmailSubject());
    mimeMessage.setSentDate(new Date());

    // set "related to send html email"
    Multipart mp = new MimeMultipart("related");

    MimeBodyPart htmlBodyPart = new MimeBodyPart();

    // set the RegistUrl and ViewGroupUrl replaceAll
    emailInfo.setEmailBody(emailInfo.getEmailBody().replaceAll(
        "emailRegistUrl", emailInfo.getEmailRegistUrl()));
    emailInfo.setEmailBody(emailInfo.getEmailBody().replaceAll(
        "emailViewGroupUrl", emailInfo.getEmailViewGroupUrl()));
    emailInfo.setEmailBody(emailInfo.getEmailBody().replaceAll("contentInfo",
        emailInfo.getEmailContent()));
    emailInfo.setEmailBody(emailInfo.getEmailBody().replaceAll("themeInfo",
        emailInfo.getEmailTheme()));

    htmlBodyPart.setDataHandler(new DataHandler(emailInfo.getEmailBody(),
        "text/html;charset=GBK"));

    mp.addBodyPart(htmlBodyPart);

    // set body pic
    FileDataSource header = new FileDataSource(new File(
        PropertyUtil.PROJECT_BASE_PATH + emailInfo.getEmailPictureName()));
    MimeBodyPart jpgBodyPart = new MimeBodyPart();
    jpgBodyPart.setDataHandler(new DataHandler(header));
    jpgBodyPart.setContentID("headerPic");
    //mp.addBodyPart(jpgBodyPart);
    // set sap header pic
    FileDataSource sapPic = new FileDataSource(new File(
        PropertyUtil.PROJECT_BASE_PATH + "images/email/sap.jpg"));
    MimeBodyPart sapPicBodyPart = new MimeBodyPart();
    sapPicBodyPart.setDataHandler(new DataHandler(sapPic));
    sapPicBodyPart.setContentID("sapPic");
    mp.addBodyPart(sapPicBodyPart);

    mimeMessage.setContent(mp);
    Transport.send(mimeMessage);
  }

  private class MyAuthenticator extends Authenticator {
    private String address;
    private String pwd;

    public MyAuthenticator(String address, String pwd) {
      this.address = address;
      this.pwd = pwd;
    }

    protected PasswordAuthentication getPasswordAuthentication() {
      return new PasswordAuthentication(address, pwd);
    }
  }
}
