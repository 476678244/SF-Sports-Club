package teamdivider.mail.timer;

public abstract class AbstractEventSpecificEmailTask extends AbstractEmailTask {

  protected String activityType;
  protected int ordinal;
  
  @Override
  public String getActivityType() {
    return this.activityType;
  }
  
  @Override
  public String toString() {
    return this.getEmailType() + "[activityType=" + activityType + ", ordinal="
        + ordinal + ", runTime=" + this.getRunTime() + ", isStarted="
        + this.isStarted() + "]";
  }
}
