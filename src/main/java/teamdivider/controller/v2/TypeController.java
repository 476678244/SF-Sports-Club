/*
 * $Id$
 */
package teamdivider.controller.v2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
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
import teamdivider.mail.MailUtil;
import teamdivider.util.ContextUtil;

@RestController
@RequestMapping("/v2")
public class TypeController {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(TypeController.class);
  
  @Autowired
  private UserDAO userDAO;

  @Autowired
  private TypeDAO typeDAO;

  @Autowired
  private EventDAO eventDAO;

  @RequestMapping("/activityTypes")
  public List<TypeVO> activityTypes() {
    List<Type> types = this.typeDAO.getAllActivityTypes(false);
    List<TypeVO> vos = new ArrayList<TypeVO>();
    for (Type type : types) {
      TypeVO vo = new TypeVO();
      vo.setName(type.getName());
      vos.add(vo);
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
      @RequestParam(value = "allEvents", defaultValue = "false") boolean allEvents,
      @RequestParam(value = "members", defaultValue = "true") boolean members) {
    List<TypeVO> types = new ArrayList<TypeVO>(1);
    Type type = this.typeDAO.getTypeByName(typeName);
    this.typeDAO.resolveTypeEvents(type).resolveTypeOrganizers(type)
        .resolveTypeScores(type);
    if (members) {
      this.typeDAO.resolveTypeSubscribers(type);
    }
    TypeVO vo = new TypeVO(type);
    EntityUtil.sortEventVOByOrdinalDescNew(vo.getEvents());
    types.add(vo);
    return types;
  }

  @RequestMapping("/activityEvent")
  public EventVO activityEvent(
      @RequestParam("activityType") String activityType,
      @RequestParam("eventId") long eventId) {
    this.typeDAO.getTypeByName(activityType, true);
    Event event = this.eventDAO.getEventByEventId(eventId, true);
    return new EventVO(event);
  }
  
  @RequestMapping("/activityEvent/continousTimes")
  public Map<String, Integer> continousTimes(
      @RequestParam("activityType") String activityType,
      @RequestParam("eventId") long eventId) {
    Type type = this.typeDAO.getTypeByName(activityType, true);
    if (type == null) return Collections.emptyMap();
    Map<String, Integer> continousTimes = new HashMap<String, Integer>();
    Set<Event> events = type.getEvents();
    List<Event> eventList = new ArrayList<Event>(events);
    EntityUtil.sortEventByOrdinalDescNew(eventList);
    // only support latest event
    if (type.getLatestEvent() == null)
      return Collections.emptyMap();
    if (type.getLatestEvent().getEventId() != eventId)
      return Collections.emptyMap();
    Set<User> continousUsers = new HashSet<User>();
    int times = 1;
    for (Event event : eventList) {
      this.eventDAO.resolveEventMembers(event);
      if (times == 1) {
        // first time, add all latestEvent joiners
        continousUsers.addAll(event.getMembers());
      } else {
        // each time, remove unjoining users
        Iterator<User> it = continousUsers.iterator();
        while (it.hasNext()) {
          User toCheckUser = it.next();
          if (!event.getMembers().contains(toCheckUser)) {
            it.remove();
          }
        }
      }
      // every round, update time record
      for (User continousUser : continousUsers) {
        continousTimes.put(continousUser.getEmail(), times);
      }
      // if no users to check anymore, return
      if (continousUsers.isEmpty()) {
        return continousTimes;
      }
      times++;
    }
    return continousTimes;
  }

  @RequestMapping("/addActivityType")
  public TypeVO addActivityType(@RequestParam("name") String name,
      @RequestParam("organizerName") String organizerName) {
    Type existingType = this.typeDAO.getTypeByName(name);
    if (existingType != null)
      return new TypeVO(existingType);
    User organizer = this.userDAO.findByEmail(organizerName);
    Type type = Type.builder().name(name).build();
    this.typeDAO.create(type);
    if (organizer != null) {
      this.typeDAO.addOrganizer(type.getTypeId(), organizer.getUserId(), organizer);
    }
    return new TypeVO(type);
  }

  @SuppressWarnings("deprecation")
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
    type.setLatestEvent(event);
    this.typeDAO.saveActivityType(type);
    return new EventVO(event);
  }

  @RequestMapping("/enrollActivityEvent")
  public EventVO enrollActivityEvent(
      @RequestParam("activityType") String activityType,
      @RequestParam("username") String username,
      @RequestParam("eventId") long eventId) {
    this.typeDAO.getTypeByName(activityType, true);
    User user = this.userDAO.findByEmail(username);
    this.eventDAO.addMember(eventId, user.getUserId(), user);
    Event event = this.eventDAO.getEventByEventId(eventId, true);
    return new EventVO(event);
  }

  @RequestMapping("/quitActivityEvent")
  public EventVO quitActivityEvent(
      @RequestParam("activityType") String activityType,
      @RequestParam("username") String username,
      @RequestParam("eventId") long eventId) {
    this.typeDAO.getTypeByName(activityType, true);
    Event event = this.eventDAO.getEventByEventId(eventId, true);
    User user = this.userDAO.findByEmail(username);
    if (event.getDrivers().contains(user)) {
      return new EventVO(event) {
        @Override
        public String getResult() {
          return "Please cancel driving at first!";
        }
      };
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
    this.typeDAO.getTypeByName(activityType, false);
    User user = this.userDAO.findByEmail(username);
    this.eventDAO.addDriver(eventId, user);
    return new EventVO(this.eventDAO.getEventByEventId(eventId, true));
  }

  @RequestMapping("/noDrivingCar")
  public EventVO noDrivingCar(@RequestParam("activityType") String activityType,
      @RequestParam("username") String username,
      @RequestParam("eventId") long eventId) {
    this.typeDAO.getTypeByName(activityType, true);
    User user = this.userDAO.findByEmail(username);
    // email notify
    List<User> passengers = this.eventDAO.getPassengers(eventId,
        user.getUserId());
    if (!passengers.isEmpty()) {
      String content = user.getFullName() + " canceled driving car in: "
          + this.eventDAO.getEventByEventId(eventId).getName();
      MailUtil.sendMail(username, passengers, "Driver change notification!",
          content, activityType, eventId);
    }
    // remove driver
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
      @RequestParam("type") String activityType, @RequestParam("eventId") long eventId) {
    this.typeDAO.getTypeByName(activityType, true);
    Event event = this.eventDAO.getEventByEventId(eventId);
    event.getGuests().add(guest);
    this.eventDAO.save(event);
    return new EventVO(event);
  }

  @RequestMapping("/removeGuest")
  public EventVO removeGuest(@RequestParam("guest") String guest,
      @RequestParam("type") String activityType, @RequestParam("eventId") long eventId) {
    this.typeDAO.getTypeByName(activityType, true);
    Event event = this.eventDAO.getEventByEventId(eventId);
    event.getGuests().remove(guest);
    this.eventDAO.save(event);
    return new EventVO(event);
  }

  @RequestMapping("/deleteActivityEvent")
  public TypeVO deleteActivityEvent(@RequestParam("type") String type,
      @RequestParam("ordinal") long eventId) {
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
    Type activityType = this.typeDAO.getTypeByName(type);
    if (activityType != null) {
      this.typeDAO.deleteType(activityType.getTypeId());
    }
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
    if (!event.getMembers().contains(passengerUser)) {
      return "{\"result\":\"Failed! Please join this event at first!\"}";
    }
    if (event.getDrivers().contains(passengerUser)) {
      return "{\"result\":\"Failed! Please don`t drive car at first!\"}";
    }
    if (event.getPassengers().get(driverUser.getId()) != null) {
      long passengers = event.getPassengers().get(driverUser.getId()).size();
      if (passengers >= 4) {
        return "{\"result\":\"Failed because there are alreday 4 passengers in this car!\"}";
      }      
    }
    this.eventDAO.addPassenger(ordinal, driverUser.getUserId(),
        passengerUser.getUserId());
    if (notification) {      
      List<User> passengers = this.eventDAO.getPassengers(event.getEventId(),
          driverUser.getUserId());
      String content = passengerUser.getFullName() + " want to by "
          + driverUser.getFullName() + "`s car. Then we have ";
      for (User user : passengers) {
        content += user.getFullName() + "; ";
      }
      content += "in " + driverUser.getFullName() + "`s car currently.";
      MailUtil.sendMail(driverUser.getEmail(), passengers,
          "Passengers change notification!", content, type, ordinal);
    }
    return "{\"result\":\"success\"}";
  }

  @RequestMapping("/notByHisCar")
  public String notByHisCar(@RequestParam("type") String type,
      @RequestParam("ordinal") long ordinal,
      @RequestParam("driver") String driver,
      @RequestParam("passenger") String passenger,
      @RequestParam(value = "notification", defaultValue = "false") boolean notification) {
    User passengerUser = this.userDAO.findByEmail(passenger);
    User driverUser = this.userDAO.findByEmail(driver);
    this.eventDAO.removePassenger(ordinal, passengerUser.getUserId());
    if (notification) {
      List<User> passengers = this.eventDAO.getPassengers(ordinal,
          driverUser.getUserId());
      String content = passengerUser.getFullName() + " don`t want to by "
          + driverUser.getFullName() + "`s car now. Then we have ";
      for (User user : passengers) {
        content += user.getFullName() + "; ";
      }
      content += "in " + driverUser.getFullName() + "`s car currently.";
      MailUtil.sendMail(driverUser.getEmail(), passengers,
          "Passengers change notification!", content, type, ordinal);
    }
    return "{\"result\":\"success\"}";
  }

  @RequestMapping("/isUserInCar")
  public boolean isUserInCar(@RequestParam("type") String type,
      @RequestParam("ordinal") long ordinal,
      @RequestParam("username") String username) {
    User user = this.userDAO.findByEmail(username);
    return this.eventDAO.isUserInCar(ordinal, user.getUserId());
  }
}
