package teamdivider.mail.timer;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import teamdivider.mail.IdentifiablelEmailTask;

public abstract class AbstractEmailTask extends TimerTask implements
    IdentifiablelEmailTask {

  private Timer timer;
  private Date runTime;
  protected boolean isStarted = false;

  @Override
  public TimerTask getTimerTask() {
    return this;
  }

  @Override
  public IdentifiablelEmailTask setTmer(Timer timer) {
    this.timer = timer;
    return this;
  }

  @Override
  public Timer getTimer() {
    return this.timer;
  }

  @Override
  public Date getRunTime() {
    return this.runTime;
  }

  @Override
  public IdentifiablelEmailTask setRunTime(Date runTime) {
    this.runTime = runTime;
    return this;
  }

  @Override
  public boolean isStarted() {
    return this.isStarted;
  }

}
