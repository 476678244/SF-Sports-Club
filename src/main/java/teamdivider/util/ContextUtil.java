package teamdivider.util;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import teamdivider.mail.MailService;
import teamdivider.repo.ActivityTypeDAO;
import teamdivider.repo.UserDAO;

@Component
public class ContextUtil {

  @Autowired
  UserDAO userDAO;

  @Autowired
  private ActivityTypeDAO activityTypeDAO;

  @Autowired
  private MailService mailService;

  public static UserDAO USER_DAO = null;

  public static ActivityTypeDAO ACTIVITY_TYPE_DAO = null;

  public static MailService MAIL_SERVICE = null;

  @PostConstruct
  void init() {
    USER_DAO = this.userDAO;
    ACTIVITY_TYPE_DAO = this.activityTypeDAO;
    MAIL_SERVICE = this.mailService;
  }
}
