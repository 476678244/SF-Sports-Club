package teamdivider.mail.timer;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import teamdivider.mail.IdentifiablelEmailTask;
import teamdivider.util.DateUtil;

public class EmailTaskPool {

  private static Map<Integer, IdentifiablelEmailTask> emailTasks = new HashMap<Integer, IdentifiablelEmailTask>();

  private static int id = 0;

  // schedule by delay
  public static Map<Integer, IdentifiablelEmailTask> scheduleTask(
      IdentifiablelEmailTask task, long delay) {
    Date runTime = new Date(new Date().getTime() + delay);
    if (!validateRunTime(runTime, task.getActivityType(), task.getEmailType())) {
      return null;
    }
    Timer timer = new Timer(true);
    timer.schedule(task.getTimerTask(), delay);
    task.setTmer(timer).setRunTime(runTime);
    emailTasks.put(generateNextId(), task);
    return emailTasks;
  }
  
  // schedule by delay
  public static Map<Integer, IdentifiablelEmailTask> scheduleByCarNotificationTask(
      SendByCarNotificationEmailTask task, long delay) {
    Date runTime = new Date(new Date().getTime() + delay);
    if (!needScheduleByCarNotificationTask(task)) {
      return emailTasks;
    }
    Timer timer = new Timer(true);
    timer.schedule(task.getTimerTask(), delay);
    task.setTmer(timer).setRunTime(runTime);
    emailTasks.put(generateNextId(), task);
    return emailTasks;
  }
  
  private static boolean needScheduleByCarNotificationTask(
      SendByCarNotificationEmailTask task) {
    if (task.getPassenger() == null)
      return true;
    for (IdentifiablelEmailTask existingTask : emailTasks.values()) {
      if (task.getEmailType().equals(existingTask.getEmailType())) {
        SendByCarNotificationEmailTask existingByCarTask = (SendByCarNotificationEmailTask) existingTask;
        if (task.getDriver().equals(existingByCarTask.getDriver())
            && task.getPassenger().equals(existingByCarTask.getPassenger())
            && !existingByCarTask.isStarted()) {
          return false;
        }
      }
    }
    return true;
  }

  // schedule by given Date runTime
  public static Map<Integer, IdentifiablelEmailTask> scheduleTask(
      IdentifiablelEmailTask task, Date runTime) {
    if (!validateRunTime(runTime, task.getActivityType(), task.getEmailType())) {
      return null;
    }
    Timer timer = new Timer(true);
    timer.schedule(task.getTimerTask(), runTime);
    task.setTmer(timer).setRunTime(runTime);
    emailTasks.put(generateNextId(), task);
    return emailTasks;
  }

  // prevent 2 times request
  private static boolean validateRunTime(Date time, String activityType,
      String emailType) {
    for (IdentifiablelEmailTask emailTask : emailTasks.values()) {
      if (emailTask.getActivityType().equals(activityType)
          && emailTask.getEmailType().equals(emailType)
          && time.getTime() - emailTask.getRunTime().getTime() < 1000 * 10) {
        return false;
      }
    }
    return true;
  }

  public static Map<Integer, IdentifiablelEmailTask> getStatus() {
    return emailTasks;
  }

  private static synchronized int generateNextId() {
    return ++id;
  }

  public static Map<Integer, IdentifiablelEmailTask> cancelEmailTask(int id) {
    emailTasks.get(id).getTimer().cancel();
    emailTasks.remove(id);
    return emailTasks;
  }

}
