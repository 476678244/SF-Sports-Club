package teamdivider.mail;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import teamdivider.util.PropertyUtil;

public class MailInfo {

  private String emailSubject;
  private String emailBody;

  private String emailRegistUrl;
  private String emailViewGroupUrl;

  private Address emailTo;
  private Address[] emailCc;

  private String emailContent = "Email main content!";
  private String emailTheme = "Theme near SAP image!";
  
  private Set<String> addresses = new HashSet<String>();

  public MailInfo(String emailSubject) {
    this.emailSubject = emailSubject;
    this.emailBody = MailUtil.getEmailBody();
  }

  public void setEmailSubject(String emailSubject) {
    this.emailSubject = emailSubject;
  }

  public String getEmailSubject() {
    return this.emailSubject;
  }

  public String getEmailBody() {
    return this.emailBody;
  }

  public MailInfo replaceForBody(String regex, String value) {
    this.emailBody = this.emailBody.replaceAll(regex, value);
    return this;
  }

  public void setEmailRegistUrl(String emailRegistUrl) {
    this.emailRegistUrl = emailRegistUrl;
  }

  public String getEmailRegistUrl() {
    return this.emailRegistUrl;
  }

  public void setEmailViewGroupUrl(String emailViewGroupUrl) {
    this.emailViewGroupUrl = emailViewGroupUrl;
  }

  public String getEmailViewGroupUrl() {
    return this.emailViewGroupUrl;
  }

  public void setEmailTo(String userEmailAddress) {
    try {
      userEmailAddress = this.addressSafeCheck(userEmailAddress)[0];
      this.emailTo = new InternetAddress(userEmailAddress);
    } catch (AddressException e) {
    } catch (Exception e) {
    }
  }

  public Address getEmailTo() {
    return this.emailTo;
  }

  private String[] addressSafeCheck(String... receivers) throws Exception {
    if (PropertyUtil.emailOnlyToZonghan()) {
      for (int i = 0; i < receivers.length; i++) {
        if (receivers[i].equals("zonghan.wu@sap.com")) {
          receivers[i] = "a476678244@163.com";
        }
        if (receivers[i].equals("lang.zongming@sap.com")) {
          receivers[i] = "476678244@qq.com";
        }
        addresses.add(receivers[i]);
      }
    }
    return receivers;
  }

  public void setEmailCc(String... receivers) throws Exception {
    addressSafeCheck(receivers);
    Address[] ccuserEmailAddress = new InternetAddress[receivers.length];
    if (receivers != null) {
      int i = 0;
      for (String address : receivers) {
        ccuserEmailAddress[i] = new InternetAddress(address);
        i++;
      }
    }
    this.emailCc = ccuserEmailAddress;
  }
  
  public void setEmailCC(Collection<String> receivers) {
    String[] ccEmailArray = new String[receivers.size()];
    try {
      this.setEmailCc(receivers.toArray(ccEmailArray));
    } catch (Exception e) {
      this.emailCc = new Address[] {};
    }
  }

  public Address[] getEmailCc() {
    return this.emailCc;
  }

  public String getEmailContent() {
    return emailContent;
  }

  public void setEmailContent(String emailContent) {
    this.emailContent = emailContent;
  }

  public String getEmailTheme() {
    return emailTheme;
  }

  public void setEmailTheme(String emailTheme) {
    this.emailTheme = emailTheme;
  }

  public Set<String> getAddresses() {
    Set<String> addresses = new HashSet<String>();
    if (this.emailTo != null) {
      addresses.add(((InternetAddress) this.emailTo).getAddress());
    }
    if (this.emailCc != null) {
      for (Address address : this.emailCc) {
        addresses.add(((InternetAddress) address).getAddress());
      }
    }
    return addresses;
  }

}
