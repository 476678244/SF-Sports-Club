package teamdivider.mail;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import teamdivider.entity.ActivityEvent;
import teamdivider.entity.User;
import teamdivider.util.ContextUtil;
import teamdivider.util.PropertyUtil;

public class MailUtil {

  public static String getEmailBody() {
    try {
      String emailFilePath = PropertyUtil.PROJECT_BASE_PATH + "email.html";
      FileInputStream fileinputstream = new FileInputStream(emailFilePath);
      int lenght = fileinputstream.available();
      byte[] bytes = new byte[lenght];
      lenght = fileinputstream.read(bytes);
      fileinputstream.close();
      return new String(bytes, 0, lenght);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public enum EmailPictureEnum {
    INVITE, ENCOURAGE, GO
  }

  public static int sendGoEmail(List<User> users, String activityType,
      int ordinal, ActivityEvent event) throws Exception {
    String viewEventLink = generateViewLink(activityType, ordinal);
    MailInfo emailInfo = new MailInfo("即刻出发！" + event.getName(),
        EmailPictureEnum.GO, activityType);
    emailInfo.setEmailTo(EmailAccountUtil.getAddress());
    List<String> ccList = new ArrayList<String>();
    for (User user : users) {
      if (event.hasUser(user.getUsername())) {
        ccList.add(user.getUsername());
      } else {
        continue;
      }
    }
    String[] ccEmailArray = new String[ccList.size()];
    ccEmailArray = ccList.toArray(ccEmailArray);
    emailInfo.setEmailCc(ccEmailArray);
    emailInfo.setEmailRegistUrl(viewEventLink);
    emailInfo.setEmailViewGroupUrl(viewEventLink);
    emailInfo.setEmailContent("出发就在此刻，不要再迟疑了！");
    emailInfo.setEmailTheme(event.getName());
    ContextUtil.MAIL_SERVICE.sendHtmlMail(emailInfo);
    return ccList.size();
  }

  public static void sendInviteEmail(User user, String baseLink,
      String activityType, int ordinal, ActivityEvent event,
      MailService mailService) throws Exception {
    String viewEventLink = generateViewLink(activityType, ordinal);
    String enrollToEventLink = generateEnrollLink(activityType, user, ordinal);
    MailInfo emailInfo = new MailInfo("快来报名:" + event.getName(),
        EmailPictureEnum.INVITE, activityType);
    emailInfo.setEmailTo(user.getUsername());
    emailInfo.setEmailRegistUrl(enrollToEventLink);
    emailInfo.setEmailViewGroupUrl(viewEventLink);
    emailInfo.setEmailContent(event.getDescription());
    emailInfo.setEmailTheme(event.getName());
    mailService.sendHtmlMail(emailInfo);
  }

  private static String generateViewLink(String activityType, int ordinal) {
    String viewEventLink = PropertyUtil.BASE_LINK
        + "/teamdivider/new/#/detail/" + activityType + "/" + ordinal;
    return viewEventLink;
  }

  private static String generateEnrollLink(String activityType, User user,
      int ordinal) {
    return PropertyUtil.BASE_LINK
        + "/teamdivider/enrollActivityEventFromEmail?activityType="
        + activityType + "&username=" + user.getUsername() + "&eventId="
        + ordinal + "&baseLink=" + PropertyUtil.BASE_LINK;
  }

  public static void sendEncourageEmail(User user, String activityType,
      int ordinal, ActivityEvent event) throws Exception {
    String viewEventLink = generateViewLink(activityType, ordinal);
    String enrollToEventLink = generateEnrollLink(activityType, user, ordinal);
    MailInfo emailInfo = new MailInfo("何不参加！"
        + event.getName(), EmailPictureEnum.ENCOURAGE, activityType);
    emailInfo.setEmailTo(user.getUsername());
    emailInfo.setEmailRegistUrl(enrollToEventLink);
    emailInfo.setEmailViewGroupUrl(viewEventLink);
    emailInfo.setEmailContent(event.getMembers().size() + event.getGuests().size()
        + " 位小伙伴已报名！ 还在犹豫什么？");
    emailInfo.setEmailTheme(event.getName());
    ContextUtil.MAIL_SERVICE.sendHtmlMail(emailInfo);
  }
  

  public static int sendByCarNotificationEmail(String activityType,
      int ordinal, ActivityEvent event, User driver, boolean stillDriver,
      Set<User> usersToTellIfNoStillDriver) throws Exception {
    String viewEventLink = generateViewLink(activityType, ordinal);
    MailInfo emailInfo = new MailInfo(driver.getFullname() + "车上最新乘坐情况—"
        + event.getName(), EmailPictureEnum.GO, activityType);
    emailInfo.setEmailTo(EmailAccountUtil.getAddress());
    List<String> ccList = new ArrayList<String>();
    if (stillDriver) {
      ccList.addAll(event.findPassengerUserNames(driver));
    } else {
      for (User user : usersToTellIfNoStillDriver) {
        ccList.add(user.getUsername());
      }
    }
    if (!ccList.contains(driver.getUsername())) {
      ccList.add(driver.getUsername());
    }
    String[] ccEmailArray = new String[ccList.size()];
    ccEmailArray = ccList.toArray(ccEmailArray);
    emailInfo.setEmailCc(ccEmailArray);
    emailInfo.setEmailRegistUrl(viewEventLink);
    emailInfo.setEmailViewGroupUrl(viewEventLink);
    String content = driver.getFullname();
    if (stillDriver) {
      content += "车上乘坐状态已经发生变化，";
      Set<String> passengers = event.getCarPassengers().get(
          driver.getUsername());
      if (passengers == null || passengers.isEmpty()) {
        content += "目前没有人在这辆车上。";
      } else {
        content += "目前坐这辆车的小伙伴有：";
        for (String passenger : passengers) {
          content += passenger + " ";
        }
      }
    } else {
      content += "取消了乘车邀请。";
    }
    emailInfo.setEmailContent(content);
    emailInfo.setEmailTheme(event.getName());
    ContextUtil.MAIL_SERVICE.sendHtmlMail(emailInfo);
    return ccList.size();
  }

}
