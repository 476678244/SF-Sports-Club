package teamdivider.util;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import teamdivider.bean.eo.Event;
import teamdivider.bean.eo.Type;
import teamdivider.bean.eo.User;
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

  private static final ThreadLocal<Context> context = new ThreadLocal<Context>();

  public static UserDAO USER_DAO = null;

  public static ActivityTypeDAO ACTIVITY_TYPE_DAO = null;

  public static MailService MAIL_SERVICE = null;

  @PostConstruct
  void init() {
    USER_DAO = this.userDAO;
    ACTIVITY_TYPE_DAO = this.activityTypeDAO;
    MAIL_SERVICE = this.mailService;
  }

  public static Context getContext() {
    if (context.get() == null) {
      context.set(new Context());
    }
    return context.get();
  }

  public static class Context {

    private Map<Long, Type> types = new HashMap<Long, Type>();

    private Map<Long, Event> events = new HashMap<Long, Event>();

    private Map<Long, User> users = new HashMap<Long, User>();

    public boolean fetchUserSubscribedTypes = false;
    
    public boolean skipQiniuActions = false;
    
    public boolean disableCache = false;

    public Context() {
    }

    public Type getType(long typeId) {
      return this.types.get(typeId);
    }

    public Event getEvent(long eventId) {
      return this.events.get(eventId);
    }

    public User getUser(long userId) {
      return this.users.get(userId);
    }

    public void setType(long typeId, Type type) {
      this.types.put(typeId, type);
    }

    public void setEvent(long eventId, Event event) {
      this.events.put(eventId, event);
    }

    public void setUser(long userId, User user) {
      this.users.put(userId, user);
    }

  }
}
