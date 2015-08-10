package teamdivider.mail.timer;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import teamdivider.entity.ActivityEvent;
import teamdivider.entity.User;
import teamdivider.mail.IdentifiablelEmailTask;
import teamdivider.mail.MailUtil;
import teamdivider.util.ContextUtil;
import teamdivider.util.PropertyUtil;

public class SendInviteEmailsTask extends AbstractEventSpecificEmailTask {

  public static Map<String, SendEmailResult> result = new HashMap<String, SendEmailResult>();

  public SendInviteEmailsTask(String activityType, int ordinal) {
    this.activityType = activityType;
    this.ordinal = ordinal;
  }

  @Override
  public void run() {
    this.isStarted = true;
    ActivityEvent event = ContextUtil.ACTIVITY_TYPE_DAO
        .getActivityEventByTypeOrdinal(activityType, ordinal);
    List<User> users = new ArrayList<User>();
    users.addAll(ContextUtil.ACTIVITY_TYPE_DAO.getActivityTypeByName(
        activityType).getSubscribers());
    for (User user : users) {
      try {
        MailUtil.sendInviteEmail(user, PropertyUtil.BASE_LINK, activityType,
            ordinal, event, ContextUtil.MAIL_SERVICE);
        result.put(event.getName() + " To: " + user.getUsername(),
            new SendEmailResult(true, event.getName()));
        Thread.sleep(1000 * 1);
      } catch (Exception e) {
        result.put(event.getName() + " To: " + user.getUsername(),
            new SendEmailResult(false, e.getMessage()));
        return;
      }
    }

  }

  @Override
  public String getEmailType() {
    return "SendInviteEmailsTask";
  }
}
