/*
 * $Id$
 */
package teamdivider.controller.v2;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
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
import teamdivider.entity.EntityUtil;

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
      @RequestParam("eventId") long eventId) {
    Event event = this.eventDAO.getEventByEventId(eventId);
    return new EventVO(event);
  }

  @RequestMapping("/addActivityType")
  public TypeVO addActivityType(@RequestParam("name") String name,
      @RequestParam("organizerName") String organizerName) {
    User organizer = this.userDAO.findByEmail(organizerName);
    Type type = Type.builder().name(name).build();
    this.typeDAO.create(type);
    this.typeDAO.addOrganizer(type.getTypeId(), organizer.getUserId(), organizer);
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
      @RequestParam("eventId") long eventId) {
    User user = this.userDAO.findByEmail(username);
    this.eventDAO.addMember(eventId, user.getUserId(), user);
    Event event = this.eventDAO.getEventByEventId(eventId);
    return new EventVO(event);
  }

  @RequestMapping("/enrollActivityEventFromEmail")
  public String enrollActivityEventFromEmail(
      @RequestParam("activityType") String activityType,
      @RequestParam("username") String username,
      @RequestParam("eventId") long ordinal,
      @RequestParam("baseLink") String baseLink) {
    this.enrollActivityEvent(activityType, username, ordinal);
    String viewEventLink = baseLink + "/teamdivider/new/#/detail/"
        + activityType + "/" + ordinal;
    return "<html>Successfully joined! Please click View Detail link to check the Activity.</html>"
        + "<a href=\"" + viewEventLink
        + "\" target=\"view_window\">View Detail!</a>";
  }

  @RequestMapping("/quitActivityEvent")
  public EventVO quitActivityEvent(
      @RequestParam("activityType") String activityType,
      @RequestParam("username") String username,
      @RequestParam("eventId") long eventId) {
    Event event = this.eventDAO.getEventByEventId(eventId, true);
    User user = this.userDAO.findByEmail(username);
    if (event.getDrivers().contains(user)) {
      return new EventVO(event);
    }
    this.eventDAO.removeUserInEvent(eventId, user.getUserId());
    return new EventVO(this.eventDAO.getEventByEventId(eventId, true));
  }

  @RequestMapping("/becomeOrganizer")
  public TypeVO becomeOrganizer(
      @RequestParam("activityType") String activityType,
      @RequestParam("username") String username) {
    Type type = this.typeDAO.getTypeByName(activityType, true);
    User user = this.userDAO.findByEmail(username);
    if (user == null)
      return null;
    if (!type.getOrganizers().contains(user)) {
      this.typeDAO.addOrganizer(type.getTypeId(), user.getUserId(), user);
    }
    return new TypeVO(this.typeDAO.getTypeByName(activityType, true));
  }

  @RequestMapping("/giveUpOrganizer")
  public TypeVO giveUpOrganizer(
      @RequestParam("activityType") String activityType,
      @RequestParam("username") String username) {
    Type type = this.typeDAO.getTypeByName(activityType, false);
    User user = this.userDAO.findByEmail(username);
    if (user == null) {
      return null;
    }
    this.typeDAO.removeOrganizer(type.getTypeId(), user.getUserId());
    return new TypeVO(this.typeDAO.getTypeByTypeId(type.getTypeId(), true));
  }

  @RequestMapping("/yesDrivingCar")
  public EventVO yesDrivingCar(
      @RequestParam("activityType") String activityType,
      @RequestParam("username") String username,
      @RequestParam("eventId") long eventId) {
    User user = this.userDAO.findByEmail(username);
    this.eventDAO.addDriver(eventId, user);
    return new EventVO(this.eventDAO.getEventByEventId(eventId, true));
  }

  @RequestMapping("/noDrivingCar")
  public EventVO noDrivingCar(@RequestParam("activityType") String activityType,
      @RequestParam("username") String username,
      @RequestParam("eventId") long eventId) {
    User user = this.userDAO.findByEmail(username);
    this.eventDAO.removeDriver(eventId, user.getUserId());
    return new EventVO(this.eventDAO.getEventByEventId(eventId, true));
  }

  @RequestMapping("/userSubscribe")
  public UserVO userSubscribe(@RequestParam("type") String type,
      @RequestParam("username") String username) {
    Type activityType = this.typeDAO.getTypeByName(type);
    User user = this.userDAO.findByEmail(username);
    this.typeDAO.userSubscribe(user.getUserId(), activityType.getTypeId(),
        user);
    return new UserVO(this.userDAO.findByUserId(user.getUserId()));
  }

  @RequestMapping("/userUnsubscribe")
  public UserVO userUnsubscribe(@RequestParam("type") String type,
      @RequestParam("username") String username) {
    Type activityType = this.typeDAO.getTypeByName(type);
    User user = this.userDAO.findByEmail(username);
    this.typeDAO.userUnSubscribe(user.getUserId(), activityType.getTypeId());
    return new UserVO(this.userDAO.findByUserId(user.getUserId()));
  }

  @RequestMapping("/addGuest")
  public EventVO addGuest(@RequestParam("guest") String guest,
      @RequestParam("type") String type, @RequestParam("eventId") long eventId) {
    Event event = this.eventDAO.getEventByEventId(eventId);
    event.getGuests().add(guest);
    this.eventDAO.save(event);
    return new EventVO(event);
  }

  @RequestMapping("/removeGuest")
  public EventVO removeGuest(@RequestParam("guest") String guest,
      @RequestParam("type") String type, @RequestParam("eventId") long eventId) {
    Event event = this.eventDAO.getEventByEventId(eventId);
    event.getGuests().remove(guest);
    this.eventDAO.save(event);
    return new EventVO(event);
  }

  @RequestMapping("/deleteActivityEvent")
  public TypeVO deleteActivityEvent(@RequestParam("type") String type,
      @RequestParam("eventId") long eventId) {
    Type activityType = this.typeDAO.getTypeByName(type, true);
    Iterator<Event> it = activityType.getEvents().iterator();
    Event latestEvent = null;
    long latestOrdinal = 0;
    while (it.hasNext()) {
      Event event = it.next();
      if (event.getEventId() == eventId) {
        it.remove();
        this.typeDAO.removeEvent(eventId);
      } else {
        if (event.getEventId() > latestOrdinal) {
          latestEvent = event;
          latestOrdinal = event.getEventId();
        }
      }
    }
    activityType.setLatestEvent(latestEvent);
    this.typeDAO.saveActivityType(activityType);
    return new TypeVO(activityType);
  }

  @RequestMapping("/deleteActivity")
  public List<TypeVO> deleteActivity(@RequestParam("type") String type) {
    Type activityType = this.typeDAO.getTypeByName(type, true);
    this.typeDAO.deleteType(activityType);
    return this.activityTypes();
  }

  @RequestMapping("/byHisCar")
  public String byHisCar(@RequestParam("type") String type,
      @RequestParam("ordinal") long ordinal,
      @RequestParam("driver") String driver,
      @RequestParam("passenger") String passenger,
      @RequestParam(value = "notification", defaultValue = "false") boolean notification) {
    Event event = this.eventDAO.getEventByEventId(ordinal, true);
    User driverUser = this.userDAO.findByEmail(driver);
    User passengerUser = this.userDAO.findByEmail(passenger);
    if (!event.getPassengers().containsValue(passengerUser.getUserId())) {
      return "{\"result\":\"By this car failed, please join this event at first!\"}";
    }
    long passengers = event.getPassengers().get(driverUser.getId()).size();
    if (passengers >= 4) {
      return "{\"result\":\"By this car failed, because there are alreday 4 passengers!\"}";
    }
    this.eventDAO.addPassenger(ordinal, driverUser.getUserId(),
        passengerUser.getUserId());
    return "{\"result\":\"success\"}";
  }

  @RequestMapping("/notByHisCar")
  public String notByHisCar(@RequestParam("type") String type,
      @RequestParam("ordinal") long ordinal,
      @RequestParam("driver") String driver,
      @RequestParam("passenger") String passenger,
      @RequestParam(value = "notification", defaultValue = "false") boolean notification) {
    User passengerUser = this.userDAO.findByEmail(passenger);
    this.eventDAO.removePassenger(ordinal, passengerUser.getUserId());
    return "{\"result\":\"success\"}";
  }

  @RequestMapping("/isUserInCar")
  public boolean isUserInCar(@RequestParam("type") String type,
      @RequestParam("ordinal") long ordinal,
      @RequestParam("username") String username) {
    Event event = this.eventDAO.getEventByEventId(ordinal, true);
    User user = this.userDAO.findByEmail(username);
    return event.getPassengers().containsValue(user.getUserId());
  }
}
