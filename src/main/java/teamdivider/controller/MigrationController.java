package teamdivider.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import teamdivider.bean.eo.Type;
import teamdivider.bean.vo.EventVO;
import teamdivider.bean.vo.TypeVO;
import teamdivider.controller.v2.TypeController;
import teamdivider.dao.TypeDAO;
import teamdivider.entity.ActivityEvent;
import teamdivider.entity.ActivityType;
import teamdivider.entity.EntityUtil;
import teamdivider.entity.User;
import teamdivider.repo.ActivityTypeDAO;
import teamdivider.repo.UserDAO;
import teamdivider.util.ContextUtil;

@RestController
public class MigrationController {

  private static final Logger log = Logger.getLogger(MigrationController.class);

  @Autowired
  private ActivityTypeDAO activityTypeDAO;

  @Autowired
  private TypeDAO typeDAO;

  @Autowired
  private teamdivider.dao.UserDAO userDAOV2;

  @Autowired
  private UserDAO userDAO;

  @Autowired
  private TypeController typeController;

  @RequestMapping("/upgradeTypeInEventForSoccer")
  public ActivityType upgradeTypeInEventForSoccer() {
    ActivityType type = this.activityTypeDAO.getActivityTypeByName("soccer");
    for (ActivityEvent event : type.getEvents()) {
      if (event.getType().isEmpty()) {
        event.setType("soccer");
      }
    }
    return this.activityTypeDAO.saveActivityType(type);
  }

  @SuppressWarnings("deprecation")
  @RequestMapping("entityToBean")
  public void entityToBean() {
    ContextUtil.getContext().disableCache = true;
    // clear data at first
    for (Type type : this.typeDAO.getAllActivityTypes()) {
      log.info("delete type:" + type.getName());
      this.typeDAO.deleteType(type.getTypeId());
    }
    for (teamdivider.bean.eo.User user : this.userDAOV2.allUsers()) {
      log.info("delete user:" + user.getEmail());
      this.userDAOV2.deleteUser(user.getUserId());
    }
    // do migration
    List<User> users = this.userDAO.allUsers();
    // create users
    for (User user : users) {
      teamdivider.bean.eo.User userV2 = new teamdivider.bean.eo.User();
      userV2.setEmail(user.getUsername());
      userV2.setAvatar(user.getAvatar());
      userV2.setFullName(user.getFullname());
      this.userDAOV2.create(userV2);
      log.info("created user : " + userV2.getEmail());
    }
    // create types
    List<ActivityType> activityTypes = this.activityTypeDAO
        .getAllActivityTypes();
    for (ActivityType activityType : activityTypes) {
      TypeVO typeVO = this.typeController.addActivityType(
          activityType.getName(), "");
      log.info("created type:" + typeVO.getName());
      for (User user : activityType.getOrganizers()) {
        this.typeController.becomeOrganizer(typeVO.getName(),
            user.getUsername());
        log.info("user (" + user.getUsername() + ") become organizer of type:"
            + typeVO.getName());
      }
      for (User user : activityType.getSubscribers()) {
        this.typeController.userSubscribe(typeVO.getName(), user.getUsername());
        log.info("user (" + user.getUsername() + ") become subscriber of type:"
            + typeVO.getName());
      }
      EntityUtil.sortEventByOrdinalAsc(activityType.getEvents());
      for (ActivityEvent activityEvent : activityType.getEvents()) {
        Calendar cal = new GregorianCalendar(2015, 1, 1);
        Date year2015 = new Date(cal.getTimeInMillis());
        String time = year2015.toString();
        try {
          Date startTime = new Date(activityEvent.getTime());
          time = startTime.toString();
        } catch (IllegalArgumentException e) {
          log.warn("can`t convert string to data of:" + activityEvent.getTime());
        }
        EventVO eventVO = this.typeController.addActivityEvent(
            typeVO.getName(), activityEvent.getName(), new Date(time),
            activityEvent.getDescription(), activityEvent.getGoTime());
        log.info("created event (" + eventVO.getName() + "/"
            + eventVO.getEventId() + ") for type:" + typeVO.getName());
        for (User user : activityEvent.getMembers()) {
          this.typeController.enrollActivityEvent(typeVO.getName(),
              user.getUsername(), eventVO.getEventId());
          log.info("user(" + user.getUsername() + ") joined event:"
              + eventVO.getEventId());
        }
        for (String guest : activityEvent.getGuests()) {
          this.typeController.addGuest(guest, typeVO.getName(),
              eventVO.getEventId());
          log.info("guest(" + guest + ") joined event:" + eventVO.getEventId());
        }
        for (User user : activityEvent.getDrivingCarMembers()) {
          this.typeController.yesDrivingCar(typeVO.getName(),
              user.getUsername(), eventVO.getEventId());
          log.info("user(" + user.getUsername() + ") drive car in event:"
              + eventVO.getEventId());
        }
        for (String driver : activityEvent.getCarPassengers().keySet()) {
          for (String passenger : activityEvent.getCarPassengers().get(driver)) {
            this.typeController.byHisCar(typeVO.getName(),
                eventVO.getEventId(), driver, this.userDAOV2
                    .findUserByFullName(passenger).getEmail(), false);
            log.info("passenger (" + passenger + ") bied user(" + driver
                + ")`s car in event:" + eventVO.getEventId());
          }
        }
      }
    }
  }
}
