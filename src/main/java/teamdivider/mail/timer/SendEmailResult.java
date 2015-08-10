package teamdivider.mail.timer;

public class SendEmailResult {

  private boolean success = false;

  private String message = null;

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public SendEmailResult(boolean success, String message) {
    this.success = success;
    this.message = message;
  }
}
