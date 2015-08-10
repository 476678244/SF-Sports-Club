package teamdivider.mail.timer;

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

public class SendGoEmailTask extends AbstractEventSpecificEmailTask {

  public static Map<String, SendEmailResult> result = new HashMap<String, SendEmailResult>();
  private List<User> users;

  public SendGoEmailTask(List<User> users, String activityType, int ordinal) {
    super();
    this.users = users;
    this.activityType = activityType;
    this.ordinal = ordinal;
  }

  @Override
  public void run() {
    this.isStarted = true;
    ActivityEvent event = ContextUtil.ACTIVITY_TYPE_DAO
        .getActivityEventByTypeOrdinal(this.activityType, this.ordinal);
    try {
      Date runTime = new Date();
      long timeOffset = runTime.getTime() - event.getGoTime().getTime();
      if (timeOffset > 5 * 60 * 1000 || timeOffset < 0) {
        throw new Exception("Run time and go time not match!");
      }
      int ccListSize = MailUtil
          .sendGoEmail(users, activityType, ordinal, event);
      result.put(event.getName() + new Date().getTime(), new SendEmailResult(
          true, String.valueOf(ccListSize)));
    } catch (Exception e) {
      result.put(this.getClass() + event.getName(), new SendEmailResult(
          false, e.getMessage()));
      e.printStackTrace();
    }
  }

  @Override
  public String getEmailType() {
    return "SendGoEmailTask";
  }
}
