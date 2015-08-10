package teamdivider.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import teamdivider.entity.ActivityEvent;
import teamdivider.entity.User;
import teamdivider.mail.EmailAccountUtil;
import teamdivider.mail.IdentifiablelEmailTask;
import teamdivider.mail.timer.EmailTaskPool;
import teamdivider.mail.timer.SendByCarNotificationEmailTask;
import teamdivider.mail.timer.SendEmailResult;
import teamdivider.mail.timer.SendEncourageEmailsTask;
import teamdivider.mail.timer.SendGoEmailTask;
import teamdivider.mail.timer.SendInviteEmailsTask;
import teamdivider.repo.ActivityTypeDAO;
import teamdivider.repo.UserDAO;
import teamdivider.util.PropertyUtil;

@RestController
public class EmailController {

  @Autowired
  private UserDAO userDAO;

  @Autowired
  private ActivityTypeDAO activityTypeDAO;

  @RequestMapping("/avaliableAddresses")
  public List<String> avaliableAddresses() {
    return EmailAccountUtil.getEmailAddresses();
  }

  @RequestMapping("/emailInvite")
  public String emailInvite(@RequestParam("activityType") String activityType,
      @RequestParam("eventId") int ordinal, @RequestParam("token") String token)
      throws Exception {
    String error = validateInviteEncouage(activityType, ordinal, token);
    if (error != null) {
      return error;
    }
    SendInviteEmailsTask task = new SendInviteEmailsTask(activityType, ordinal);
    Map<Integer, IdentifiablelEmailTask> IdentifiablelEmailTasks = EmailTaskPool
        .scheduleTask(task, 1000 * 1);
    return "Successfully scheduled Invite email task!";
  }

  private String validateInviteEncouage(String type, int ordinal, String token) {
    if (!"token".equals(token)) {
      return generateResult("error token!");
    }
    ActivityEvent event = this.activityTypeDAO.getActivityEventByTypeOrdinal(
        type, ordinal);
    if (new Date().after(event.getGoTime())) {
      return generateResult("This activity event is out of time!");
    }
    return null;
  }

  private String generateResult(String key) {
    return "Sending email failed : " + key;
  }

  @RequestMapping("/getSendEmailResult")
  public Map<String, SendEmailResult> getSendEmailResult(
      @RequestParam("type") String type) {
    if (type.equals("invite")) {
      return SendInviteEmailsTask.result;
    } else if (type.equals("go")) {
      return SendGoEmailTask.result;
    } else if (type.equals("go")) {
      return SendEncourageEmailsTask.result;
    } else if (type.equals("bycar")) {
      return SendByCarNotificationEmailTask.result;
    } else {
      return SendInviteEmailsTask.result;
    }
  }

  @RequestMapping("/scheduleGoEmail")
  public String scheduleGoEmail(
      @RequestParam("activityType") String activityType,
      @RequestParam("eventId") int ordinal,
      @RequestParam("token") String token) throws Exception {
    if (!"token".equals(token)) {
      return generateResult("error token!");
    }
    ActivityEvent event = this.activityTypeDAO.getActivityEventByTypeOrdinal(
        activityType, ordinal);
    List<User> users = this.activityTypeDAO.getActivityTypeByName(activityType)
        .getSubscribers();
    SendGoEmailTask task = new SendGoEmailTask(users, activityType, ordinal);
    Map<Integer, IdentifiablelEmailTask> IdentifiablelEmailTasks = EmailTaskPool
        .scheduleTask(task, event.getGoTime());
    return "Successfully scheduled Go email task!";
  }

  @RequestMapping("/scheduleEncourageEmail")
  public String scheduleEncourageEmail(
      @RequestParam("activityType") String activityType,
      @RequestParam("eventId") int ordinal, @RequestParam("token") String token)
      throws Exception {
    String error = validateInviteEncouage(activityType, ordinal, token);
    if (error != null) {
      return error;
    }
    SendEncourageEmailsTask task = new SendEncourageEmailsTask(activityType,
        ordinal);
    Map<Integer, IdentifiablelEmailTask> IdentifiablelEmailTasks = EmailTaskPool
        .scheduleTask(task, 1000 * 1);
    return "Successfully scheduled Encourage email task!";
  }

  @RequestMapping("/getEmailTasks")
  public Map<Integer, String> getEmailTasks() {
    Map<Integer, IdentifiablelEmailTask> status = EmailTaskPool.getStatus();
    return this.getResultByStatus(status);
  }

  @RequestMapping("/cancelIdentifiablelEmailTask")
  public Map<Integer, String> cancelIdentifiablelEmailTask(@RequestParam("id") int id) {
    Map<Integer, IdentifiablelEmailTask> status = EmailTaskPool.cancelEmailTask(id);
    return this.getResultByStatus(status);
  }

  private Map<Integer, String> getResultByStatus(Map<Integer, IdentifiablelEmailTask> status) {
    Map<Integer, String> result = new HashMap<Integer, String>(status.size());
    for (int id : status.keySet()) {
      result.put(id, status.get(id).toString());
    }
    return result;
  }
}
