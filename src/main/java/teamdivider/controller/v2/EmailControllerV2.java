package teamdivider.controller.v2;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import teamdivider.bean.eo.Event;
import teamdivider.bean.eo.Type;
import teamdivider.bean.eo.User;
import teamdivider.dao.EventDAO;
import teamdivider.dao.TypeDAO;
import teamdivider.dao.UserDAO;
import teamdivider.mail.MailUtil;
import teamdivider.mail.EmailInvitesTracker;

@RestController
@RequestMapping("/v2")
public class EmailControllerV2 {

  private static final Logger log = Logger.getLogger(EmailControllerV2.class);
  
  @Autowired
  private UserDAO userDAO;

  @Autowired
  private TypeDAO typeDAO;

  @Autowired
  private EventDAO eventDAO;

  @RequestMapping("/emailInvite")
  public String emailInvite(@RequestParam("activityType") String activityType,
      @RequestParam("eventId") int eventId,
      @RequestParam("token") String token) {
    String error = validateInviteEncouage(activityType, eventId, token);
    if (error != null) {
      return error;
    }
    Event event = this.eventDAO.getEventByEventId(eventId);
    Type type = this.typeDAO.getTypeByName(activityType, true);
    // after validation, apply for approval
    if (EmailInvitesTracker.getApproval(event.getEventId())) {
      for (User subscriber : type.getSubscribers()) {
        MailUtil.sendMail(subscriber.getEmail(), Collections.emptySet(),
            "邀请您参加！", event.getDescription(), activityType, eventId);     
      }
    } else {
      log.warn("EmailInvitesTracker.getApproval failed for event:" + event.getName());
    }
    return "Successfully scheduled Invite email task!";
  }
  
  @RequestMapping("/status")
  public Map<Long, Date> status() {
    return EmailInvitesTracker.status();
  }
  
  @RequestMapping("/clearTrackStatus")
  public Map<Long, Date> clearTrackStatus() {
    EmailInvitesTracker.clear();
    return EmailInvitesTracker.status();
  }

  @RequestMapping("/email/notifySubscribers")
  public void notifySubscribers(@RequestParam("typeId") long typeId,
      @RequestParam("content") String content,
      @RequestParam("userId") long userId) {
    User user = this.userDAO.findByUserId(userId);
    Type type = this.typeDAO.getTypeByTypeId(typeId);
    this.typeDAO.resolveTypeSubscribers(type);
    MailUtil.sendMail(user.getEmail(), type.getSubscribers(),
        "Organizer " + user.getFullName() + " are saying to club members:",
        content, type.getName(), type.getLatestEvent().getEventId());
  }

  @RequestMapping("/email/notifyEventJoiners")
  public void notifySubscribers(@RequestParam("eventId") long eventId,
      @RequestParam("typeName") String typeName,
      @RequestParam("content") String content,
      @RequestParam("userId") long userId) {
    User user = this.userDAO.findByUserId(userId);
    Event event = this.eventDAO.getEventByEventId(eventId);
    this.eventDAO.resolveEventMembers(event);
    MailUtil.sendMail(user.getEmail(), event.getMembers(),
        "Organizer " + user.getFullName() + " are saying to event joiners:",
        content, typeName, eventId);
  }

  private String validateInviteEncouage(String type, int eventId,
      String token) {
    if (!"token".equals(token)) {
      return generateResult("error token!");
    }
    Event event = this.eventDAO.getEventByEventId(eventId);
    if (new Date().after(event.getGoTime())) {
      return generateResult("This activity event is out of time!");
    }
    return null;
  }

  private String generateResult(String key) {
    return "Sending email failed : " + key;
  }
}
