package teamdivider.controller.v2;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

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

@RestController
@RequestMapping("/v2")
public class EmailControllerV2 {

  @Autowired
  private UserDAO userDAO;

  @Autowired
  private TypeDAO typeDAO;

  @Autowired
  private EventDAO eventDAO;
  
  @RequestMapping("/emailInvite")
  public String emailInvite(@RequestParam("activityType") String activityType,
      @RequestParam("eventId") int eventId, @RequestParam("token") String token) {
    String error = validateInviteEncouage(activityType, eventId, token);
    if (error != null) {
      return error;
    }
    Event event = this.eventDAO.getEventByEventId(eventId);
    Type type = this.typeDAO.getTypeByName(activityType, true);
    Collection<String> addresses = new HashSet<String>();
    for (User user : type.getSubscribers()) {
      addresses.add(user.getEmail());
    }
    MailUtil.sendMail("zonghan.wu@sap.com", addresses, "邀请您参加！",
        event.getDescription(), activityType, eventId);
    return "Successfully scheduled Invite email task!";
  }
  
  private String validateInviteEncouage(String type, int eventId, String token) {
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
