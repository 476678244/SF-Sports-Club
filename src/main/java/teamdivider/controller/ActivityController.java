package teamdivider.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import teamdivider.entity.ActivityEvent;
import teamdivider.entity.ActivityType;
import teamdivider.entity.EntityUtil;
import teamdivider.entity.User;
import teamdivider.mail.timer.EmailTaskPool;
import teamdivider.mail.timer.SendByCarNotificationEmailTask;
import teamdivider.repo.ActivityTypeDAO;
import teamdivider.repo.UserDAO;
import teamdivider.util.CacheUtil;
import teamdivider.util.CacheUtil.TypeCache;
import teamdivider.util.CacheUtil.TypeNamesCache;
import teamdivider.util.PropertyUtil;

@RestController
public class ActivityController {

  @Autowired
  private UserDAO userDAO;
  
  @Autowired
  private ActivityTypeDAO activityTypeDAO;
  
  @RequestMapping("/activityTypes")
  public List<ActivityType> activityTypes() {
    Set<String> kindTypes = this.activityTypeDAO.getAllKindTypes();
    List<ActivityType> activityNames = new ArrayList<ActivityType>();
    for (String type : kindTypes) {
      ActivityType name = new ActivityType();
      name.setName(type);
      activityNames.add(name);
    }
    EntityUtil.sortTypesByPriorityDesc(activityNames);
    return activityNames;
  }
  
  @RequestMapping("/activityTypes/joining")
  public List<ActivityType> joiningTypes(@RequestParam("username") String username) {
    User user = this.userDAO.findByUsername(username);
    List<ActivityType> activityNames = new ArrayList<ActivityType>();
    for (String type : user.getSubscribedTypes()) {
      ActivityType name = new ActivityType();
      name.setName(type);
      activityNames.add(name);
    }
    EntityUtil.sortTypesByPriorityDesc(activityNames);
    return activityNames;
  }
  
  @RequestMapping("/activityType")
  public List<ActivityType> activityType(@RequestParam("activityType") String activityType,
      @RequestParam(value="allEvents", defaultValue="false") boolean allEvents) {
    List<ActivityType> types = new ArrayList<ActivityType>(1);
    ActivityType type = this.activityTypeDAO.getTypeByNameFromDB(activityType);
    ActivityType target = type.simpleType();
    if (!allEvents) {
      if (target.getEvents().size() >= PropertyUtil.SHOW_EVENTS_NUMBER) {
        target.setEvents(target.getEvents().subList(0,
            PropertyUtil.SHOW_EVENTS_NUMBER));
      }
    }
    types.add(target);
    return types;
  }
  
  @RequestMapping("/activityEvent")
  public ActivityEvent activityEvent(@RequestParam("activityType") String activityType,
      @RequestParam("eventId") int ordinal) {
    ActivityEvent event = this.activityTypeDAO.getActivityEventByTypeOrdinal(activityType, ordinal);
    return event;
  }
  
  @RequestMapping("/addActivityType")
  public ActivityType addActivityType(@RequestParam("name") String name,
      @RequestParam("organizerName") String organizerName) {
    User organizer = this.userDAO.findByUsername(organizerName);
    ActivityType type = new ActivityType(name, organizer);
    type = this.activityTypeDAO.saveActivityType(type);
    CacheUtil.removeTypeNamesCache();
    return type;
  }
  
  @RequestMapping("/addActivityEvent")
  public ActivityEvent addActivityEvent(@RequestParam("activityType") String activityType,
      @RequestParam("name") String name, @RequestParam("time") String time,
      @RequestParam("description") String description, @RequestParam("goTime") Date goTime) {
    ActivityType type = this.activityTypeDAO.getActivityTypeByName(activityType);
    ActivityEvent event = new ActivityEvent(type.getLatestOrdinal() + 1, name, time, goTime);
    event.setType(activityType);
    event.setDescription(description);
    if (type.hasEvent(event) != null) {
      return type.hasEvent(event);
    }
    type.addEvent(event);
    type = this.activityTypeDAO.saveActivityType(type);
    return type.getEventByOrdinal(event.getOrdinal());
  }
  
  @RequestMapping("/enrollActivityEvent")
  public ActivityEvent enrollActivityEvent(@RequestParam("activityType") String activityType,
      @RequestParam("username") String username, @RequestParam("eventId") int ordinal) {
    String lock = CacheUtil.getInCacheTypeString(activityType);
    synchronized (lock) {
      ActivityType type = this.activityTypeDAO.getActivityTypeByName(activityType);
      ActivityEvent event = type.getEventByOrdinal(ordinal);
      event.addMember(this.userDAO.findByUsername(username));
      type = this.activityTypeDAO.saveActivityType(type);
      return type.getEventByOrdinal(ordinal);
    }
  }
  
  @RequestMapping("/enrollActivityEventFromEmail")
  public String enrollActivityEventFromEmail(@RequestParam("activityType") String activityType,
      @RequestParam("username") String username, @RequestParam("eventId") int ordinal,
      @RequestParam("baseLink") String baseLink) {
    String lock = CacheUtil.getInCacheTypeString(activityType);
    synchronized (lock) {
      this.enrollActivityEvent(activityType, username, ordinal);
      String viewEventLink = baseLink + "/teamdivider/new/#/detail/"
          + activityType + "/" + ordinal;
      return "<html>Successfully joined! Please click View Detail link to check the Activity.</html>"
          + "<a href=\""
          + viewEventLink
          + "\" target=\"view_window\">View Detail!</a>";
    }
  }
  
  @RequestMapping("/quitActivityEvent")
  public ActivityEvent quitActivityEvent(@RequestParam("activityType") String activityType,
      @RequestParam("username") String username, @RequestParam("eventId") int ordinal) {
    String lock = CacheUtil.getInCacheTypeString(activityType);
    synchronized (lock) {
      ActivityType type = this.activityTypeDAO
          .getActivityTypeByName(activityType);
      ActivityEvent event = type.getEventByOrdinal(ordinal);
      User user = this.userDAO.findByUsername(username);
      if (event.getDrivingCarMembers().contains(user)) {
        event.setResult("Please cancel driving at first!");
        return event;
      }
      event.removeMember(user);
      type = this.activityTypeDAO.saveActivityType(type);
      return type.getEventByOrdinal(ordinal);
    }
  }
  
  @RequestMapping("/becomeOrganizer")
  public ActivityType becomeOrganizer(@RequestParam("activityType") String activityType,
      @RequestParam("username") String username) {
    ActivityType type = this.activityTypeDAO.getActivityTypeByName(activityType);
    User user = this.userDAO.findByUsername(username);
    if (user == null) {
      return type;
    }
    if (!type.getOrganizers().contains(user)) {
      type.getOrganizers().add(user); 
      return this.activityTypeDAO.saveActivityType(type);
    }
    return type;
  }
  
  @RequestMapping("/giveUpOrganizer")
  public ActivityType giveUpOrganizer(@RequestParam("activityType") String activityType,
      @RequestParam("username") String username) {
    ActivityType type = this.activityTypeDAO.getActivityTypeByName(activityType);
    User user = this.userDAO.findByUsername(username);
    if (user == null) {
      return type;
    }
    type.getOrganizers().remove(user);
    return this.activityTypeDAO.saveActivityType(type);
  }
  
  @RequestMapping("/yesDrivingCar")
  public ActivityEvent yesDrivingCar(
      @RequestParam("activityType") String activityType,
      @RequestParam("username") String username,
      @RequestParam("eventId") int ordinal) {
    String lock = CacheUtil.getInCacheTypeString(activityType);
    synchronized (lock) {
      ActivityType type = this.activityTypeDAO
          .getActivityTypeByName(activityType);
      ActivityEvent event = type.getEventByOrdinal(ordinal);
      event.yesDrivingCar(this.userDAO.findByUsername(username));
      type = this.activityTypeDAO.saveActivityType(type);
      return type.getEventByOrdinal(ordinal);
    }
  }
  
  @RequestMapping("/noDrivingCar")
  public ActivityEvent noDrivingCar(
      @RequestParam("activityType") String activityType,
      @RequestParam("username") String username,
      @RequestParam("eventId") int ordinal) {
    String lock = CacheUtil.getInCacheTypeString(activityType);
    synchronized (lock) {
      ActivityType type = this.activityTypeDAO
          .getActivityTypeByName(activityType);
      User driver = this.userDAO.findByUsername(username);
      ActivityEvent event = type.getEventByOrdinal(ordinal);
      Set<User> usersToTellIfNoStillDriver = event.getPassengers(driver);
      event.noDrivingCar(driver);
      type = this.activityTypeDAO.saveActivityType(type);
      SendByCarNotificationEmailTask task = new SendByCarNotificationEmailTask(
          activityType, ordinal, this.userDAO.findByUsername(username), null,
          false);
      task.addUsersToTellIfNotStillDriver(usersToTellIfNoStillDriver);
      EmailTaskPool.scheduleByCarNotificationTask(task, 1000 * 5);
      return type.getEventByOrdinal(ordinal);
    }
  }
  
  @RequestMapping("/userSubscribe")
  public User userSubscribe(@RequestParam("type") String type,
      @RequestParam("username") String username) {
    ActivityType activityType = this.activityTypeDAO
        .getActivityTypeByName(type);
    activityType.userSubscribe(this.userDAO.findByUsername(username));
    this.activityTypeDAO.saveActivityType(activityType);
    return this.userDAO.findByUsername(username);
  }
  
  @RequestMapping("/userUnsubscribe")
  public User userUnsubscribe(@RequestParam("type") String type,
      @RequestParam("username") String username) {
    ActivityType activityType = this.activityTypeDAO
        .getActivityTypeByName(type);
    activityType.unSubscribe(username);
    this.activityTypeDAO.saveActivityType(activityType);
    return this.userDAO.findByUsername(username);
  }
  
  @RequestMapping("/addGuest")
  public ActivityEvent addGuest(@RequestParam("guest") String guest,
      @RequestParam("type") String type, @RequestParam("eventId") int ordinal) {
    String lock = CacheUtil.getInCacheTypeString(type);
    synchronized (lock) {
      ActivityType activityType = this.activityTypeDAO
          .getActivityTypeByName(type);
      ActivityEvent event = activityType.getEventByOrdinal(ordinal);
      event.addGuest(guest);
      activityType = this.activityTypeDAO.saveActivityType(activityType);
      return event;
    }
  }
  
  @RequestMapping("/removeGuest")
  public ActivityEvent removeGuest(@RequestParam("guest") String guest,
      @RequestParam("type") String type, @RequestParam("eventId") int ordinal) {
    String lock = CacheUtil.getInCacheTypeString(type);
    synchronized (lock) {
      ActivityType activityType = this.activityTypeDAO
          .getActivityTypeByName(type);
      ActivityEvent event = activityType.getEventByOrdinal(ordinal);
      event.removeGuest(guest);
      activityType = this.activityTypeDAO.saveActivityType(activityType);
      return event;
    }
  }
  
  @RequestMapping("/deleteActivityEvent")
  public ActivityType deleteActivityEvent(@RequestParam("type") String type,
      @RequestParam("ordinal") int ordinal) {
    String lock = CacheUtil.getInCacheTypeString(type);
    synchronized (lock) {
      ActivityType activityType = this.activityTypeDAO
          .getActivityTypeByName(type);
      Iterator<ActivityEvent> it = activityType.getEvents().iterator();
      int latestOrdinal = 0;
      while (it.hasNext()) {
        ActivityEvent event = it.next();
        if (event.getOrdinal() == ordinal) {
          it.remove();
        } else {
          if (event.getOrdinal() > latestOrdinal) {
            latestOrdinal = event.getOrdinal();
          }
        }
      }
      activityType.setLatestOrdinal(latestOrdinal);
      return this.activityTypeDAO.saveActivityType(activityType);
    }
  }

  @RequestMapping("/deleteActivity")
  public List<ActivityType> deleteActivity(@RequestParam("type") String type) {
    ActivityType activityType = this.activityTypeDAO.getActivityTypeByName(type);
    this.activityTypeDAO.deleteActivityType(activityType);
    return this.activityTypes();
  }
  
  @RequestMapping("/byHisCar")
  public String byHisCar(@RequestParam("type") String type,
      @RequestParam("ordinal") int ordinal,
      @RequestParam("driver") String driver,
      @RequestParam("passenger") String passenger,
      @RequestParam(value="notification", defaultValue="false") boolean notification) {
    String lock = CacheUtil.getInCacheTypeString(type);
    synchronized (lock) {
      ActivityType activityType = this.activityTypeDAO
          .getActivityTypeByName(type);
      ActivityEvent event = activityType.getEventByOrdinal(ordinal);
      User driverUser = this.userDAO.findByUsername(driver);
      User passengerUser = this.userDAO.findByUsername(passenger);
      if (!event.hasUser(passenger)) {
        return "{\"result\":\"By this car failed, please join this event at first!\"}";
      }
      int passengers = event.byHisCar(driverUser, passengerUser);
      if (passengers >= 5) {
        return "{\"result\":\"By this car failed, because there are alreday 4 passengers!\"}";
      }
      this.activityTypeDAO.saveActivityType(activityType);
      if (notification) {
        SendByCarNotificationEmailTask task = new SendByCarNotificationEmailTask(
            type, ordinal, driverUser, passengerUser, false);
        EmailTaskPool.scheduleByCarNotificationTask(task, 1000 * 30);
      }
      return "{\"result\":\"success\"}";
    }
  }
  
  @RequestMapping("/notByHisCar")
  public String notByHisCar(@RequestParam("type") String type,
      @RequestParam("ordinal") int ordinal,
      @RequestParam("driver") String driver,
      @RequestParam("passenger") String passenger,
      @RequestParam(value="notification", defaultValue="false") boolean notification) {
    String lock = CacheUtil.getInCacheTypeString(type);
    synchronized (lock) {
      ActivityType activityType = this.activityTypeDAO
          .getActivityTypeByName(type);
      ActivityEvent event = activityType.getEventByOrdinal(ordinal);
      User driverUser = this.userDAO.findByUsername(driver);
      User passengerUser = this.userDAO.findByUsername(passenger);
      if (!event.inHisCar(driverUser, passengerUser)) {
        return "{\"result\":\"By this car failed, because you are alreday in one Car!\"}";
      }
      event.notByHisCar(driverUser, passengerUser);
      this.activityTypeDAO.saveActivityType(activityType);
      if (notification) {
        SendByCarNotificationEmailTask task = new SendByCarNotificationEmailTask(
            type, ordinal, driverUser, passengerUser, true);
        EmailTaskPool.scheduleByCarNotificationTask(task, 1000 * 30);
      }
      return "{\"result\":\"success\"}";
    }
  }

  @RequestMapping("/isUserInCar")
  public boolean isUserInCar(@RequestParam("type") String type,
      @RequestParam("ordinal") int ordinal,
      @RequestParam("username") String username) {
    ActivityEvent event = this.activityTypeDAO.getActivityEventByTypeOrdinal(
        type, ordinal);
    return event.getInCarUsers().contains(username);
  }
  
  @RequestMapping("/typesCache")
  public Map<String, TypeCache> printTypesCache() {
    return CacheUtil.printTypesCache();
  }
  
  @RequestMapping("/typeNamesCache")
  public TypeNamesCache printTypeNamesCache() {
    return CacheUtil.printTypeNamesCache();
  }
}
