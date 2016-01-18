/*
 * $Id$
 */
package teamdivider.controller.v2;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import teamdivider.bean.eo.Event;
import teamdivider.bean.eo.Type;
import teamdivider.bean.eo.User;
import teamdivider.bean.vo.EventVO;
import teamdivider.bean.vo.TypeVO;
import teamdivider.bean.vo.UserVO;
import teamdivider.dao.EventDAO;
import teamdivider.dao.TypeDAO;
import teamdivider.dao.UserDAO;
import teamdivider.entity.ActivityEvent;
import teamdivider.entity.ActivityType;
import teamdivider.entity.EntityUtil;
import teamdivider.util.CacheUtil;
import teamdivider.util.PropertyUtil;

@RestController
public class TypeController {

  @Autowired
  private UserDAO userDAO;

  @Autowired
  private TypeDAO typeDAO;
  
  @Autowired
  private EventDAO eventDAO;

  @RequestMapping("/activityTypes")
  public List<TypeVO> activityTypes() {
    List<Type> types = this.typeDAO.getAllActivityTypes(true);
    List<TypeVO> vos = new ArrayList<TypeVO>();
    for (Type type : types) {
      vos.add(new TypeVO(type));
    }
    EntityUtil.sortTypeVOsByPriorityDesc(vos);
    return vos;
  }
  
  @RequestMapping("/activityTypes/joining")
  public List<TypeVO> joiningTypes(@RequestParam("username") String username) {
    User user = this.userDAO.findByEmail(username);
    List<TypeVO> activityNames = new ArrayList<TypeVO>();
    for (Type type : user.getSubscribedTypes()) {
      activityNames.add(new TypeVO(type));
    }
    EntityUtil.sortTypeVOsByPriorityDesc(activityNames);
    return activityNames;
  }
  
  @RequestMapping("/activityType")
  public List<TypeVO> activityType(
      @RequestParam("activityType") String typeName,
      @RequestParam(value = "allEvents", defaultValue = "false") boolean allEvents) {
    List<TypeVO> types = new ArrayList<TypeVO>(1);
    Type type = this.typeDAO.getTypeByName(typeName);
    types.add(new TypeVO(type));
    return types;
  }
  
  @RequestMapping("/activityEvent")
  public EventVO activityEvent(
      @RequestParam("activityType") String activityType,
      @RequestParam("eventId") int eventId) {
    Event event = this.eventDAO.getEventByEventId(eventId);
    return new EventVO(event);
  }
  
  @RequestMapping("/addActivityType")
  public TypeVO addActivityType(@RequestParam("name") String name,
      @RequestParam("organizerName") String organizerName) {
    User organizer = this.userDAO.findByEmail(organizerName);
    Type type = Type.builder().name(name).build();
    type.getOrganizers().add(organizer);
    this.typeDAO.create(type);
    return new TypeVO(type);
  }
  
  @RequestMapping("/addActivityEvent")
  public EventVO addActivityEvent(
      @RequestParam("activityType") String activityType,
      @RequestParam("name") String name, @RequestParam("time") String time,
      @RequestParam("description") String description,
      @RequestParam("goTime") Date goTime) {
    Type type = this.typeDAO.getTypeByName(activityType, true);
    Event event = Event.builder().name(name).description(description)
        .startTime(new Date(time)).goTime(goTime).typeId(type.getTypeId())
        .build();
    if (type.hasEvent(event) != null) {
      return new EventVO(type.hasEvent(event));
    }
    this.eventDAO.create(event);
    this.typeDAO.addEvent(event);
    return new EventVO(event);
  }
  
  @RequestMapping("/enrollActivityEvent")
  public EventVO enrollActivityEvent(
      @RequestParam("activityType") String activityType,
      @RequestParam("username") String username,
      @RequestParam("eventId") int eventId) {
    User user = this.userDAO.findByEmail(username);
    this.eventDAO.addMember(eventId, user.getUserId(), user);
    Event event = this.eventDAO.getEventByEventId(eventId);
    return new EventVO(event);
  }
}
