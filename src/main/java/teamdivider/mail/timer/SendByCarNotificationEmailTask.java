package teamdivider.mail.timer;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import teamdivider.entity.ActivityEvent;
import teamdivider.entity.User;
import teamdivider.mail.IdentifiablelEmailTask;
import teamdivider.mail.MailUtil;
import teamdivider.util.ContextUtil;
import teamdivider.util.PropertyUtil;

public class SendByCarNotificationEmailTask extends AbstractEventSpecificEmailTask implements
    IdentifiablelEmailTask {

  private User driver;
  private User passenger;
  private boolean fromInCarState;
  public static Map<String, SendEmailResult> result = new HashMap<String, SendEmailResult>();
  private Set<User> usersToTellIfNoStillDriver = new HashSet<User>();

  public SendByCarNotificationEmailTask(String activityType, int ordinal,
      User driver, User passenger, boolean fromInCarState) {
    this.activityType = activityType;
    this.ordinal = ordinal;
    this.driver = driver;
    this.passenger = passenger;
    this.fromInCarState = fromInCarState;
  }

  @Override
  public String getEmailType() {
    return "SendByCarNotificationEmailTask";
  }

  @Override
  public void run() {
    ActivityEvent event = ContextUtil.ACTIVITY_TYPE_DAO
        .getActivityEventByTypeOrdinal(this.activityType, this.ordinal);
    this.isStarted = true;
    boolean needSendEmail = false;
    boolean currentlyAsDriver = this.currentlyAsDriver(event, driver);
    if (!currentlyAsDriver) {
      if (this.usersToTellIfNoStillDriver.isEmpty()) {
        return ;
      }
      needSendEmail = true;
    } else {
      boolean currentlyIncar = this.currentlyInCar(event, driver, passenger);
      if (fromInCarState != currentlyIncar) {
        needSendEmail = true;
      }
    }
    if (needSendEmail) {
      try {
        int ccListSize = MailUtil.sendByCarNotificationEmail(activityType,
            ordinal, event, driver, currentlyAsDriver,
            this.usersToTellIfNoStillDriver);
        result.put(event.getName() + new Date().getTime(), new SendEmailResult(
            true, String.valueOf(ccListSize)));
      } catch (Exception e) {
        result.put(this.getClass() + event.getName(), new SendEmailResult(
            false, e.getMessage()));
        e.printStackTrace();
      }
    }
  }
  
  private boolean currentlyInCar(ActivityEvent event, User driver, User passenger) {
    Map<String, Set<String>> carPassengers = event.getCarPassengers();
    for (String driverUsername : carPassengers.keySet()) {
      if (driverUsername.equals(driver.getUsername())) {
        if (carPassengers.get(driverUsername) == null)
          return false;
        return carPassengers.get(driverUsername).contains(passenger.getFullname());
      }
    }
    return false;
  }
  
  private boolean currentlyAsDriver(ActivityEvent event, User driver) {
    return event.getDrivingCarMembers().contains(driver);
  }
  
  public User getDriver() {
    return this.driver;
  }
  
  public User getPassenger() {
    return this.passenger;
  }
  
  public void addUsersToTellIfNotStillDriver(Set<User> users) {
    this.usersToTellIfNoStillDriver.addAll(users);
  }

}
