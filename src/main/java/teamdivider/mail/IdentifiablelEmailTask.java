package teamdivider.mail;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public interface IdentifiablelEmailTask {

  String getEmailType();
  
  String getActivityType();
  
  TimerTask getTimerTask();
  
  IdentifiablelEmailTask setTmer(Timer timer);
  
  Timer getTimer();
  
  Date getRunTime();
  
  IdentifiablelEmailTask setRunTime(Date runTime);
  
  boolean isStarted();

}
