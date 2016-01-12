package teamdivider.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import teamdivider.engine.Fendui;
import teamdivider.entity.ActivityEvent;
import teamdivider.entity.ActivityType;
import teamdivider.entity.Team;
import teamdivider.entity.User;
import teamdivider.repo.ActivityTypeDAO;
import teamdivider.repo.BaseData;
import teamdivider.repo.UserDAO;

@RestController
public class DemoController {
  
  @Autowired
  private BaseData baseData;

  @Autowired
  private UserDAO userDAO;
  
  @Autowired
  private ActivityTypeDAO activityTypeDAO;
  
  @Autowired
  private ScoreController scoreController;
  
  @RequestMapping("/fendui")
  public List<Team> fendui(
      @RequestParam(value="numberTeams", defaultValue="2") int numberTeams,
      @RequestParam(value="reFenDui", defaultValue="false") boolean reFenDui,
      @RequestParam(value="username", defaultValue="") String username,
      @RequestParam(value="eventId", defaultValue="0") int ordinal) {
    soccer = this.baseData.getSoccer();
    ActivityEvent event = null;
    if (ordinal == 0) {
      event = soccer.getEvents().get(0);
    } else {
      event = soccer.getEventByOrdinal(ordinal); 
    }
    if (isOrganizer(username, soccer)) {
      if (reFenDui) {
        soccer.loadUsersScore(this.userDAO.allUsers());
    	syncMemberScore(soccer, event);
    	event.updateFenDuiResult(Fendui.fenDui(soccer, event, numberTeams));
        soccer = this.baseData.saveExampleActivityType(soccer);
      } else {
        return event.getFenDuiResult(numberTeams);
      }
    } else {
      return event.getFenDuiResult(numberTeams);
    }
    return event.getFenDuiResult(numberTeams);
  }
  
  // the score field of User is just temp score
  // need to sync real time score before using fen dui engine
  private void syncMemberScore(ActivityType type, ActivityEvent event) {
    List<User> members = event.getMembers();
    for (User member : members) {
      int score = type.getUserScore(member.getUsername());
      member.setScore(score);
    }
  }
  
  @RequestMapping("/install")
  public String install() {
    this.baseData.clearDB();
    this.baseData.mergeUsers();
    soccer = this.installSoccer();
    this.scoreController.installScoreStatistic();
    this.subscribeUsersToSoccer(soccer);
    return "Success!";
  }

  @RequestMapping("/installSoccer")
  public ActivityType installSoccer() {
    return this.baseData.installSoccer();
  }
  
  @RequestMapping("/joinHistory")
  public Map<User, Integer> joinHistory() {
    Map<User, Integer> history = new HashMap<User, Integer>();
    List<ActivityEvent> events = this.activityTypeDAO.getActivityTypeByName(
        "soccer").getEvents();
    for (ActivityEvent event : events) {
      for (User user : event.getMembers()) {
        if (history.get(user) == null) {
          history.put(user, 1);
        } else {
          int inc = history.get(user) + 1;
          history.put(user, inc);
        }
      }
    }
    return history;
  }
  
  private void subscribeUsersToSoccer(ActivityType soccer) {
    soccer.getSubscribers().addAll(this.userDAO.allUsers());
    this.activityTypeDAO.saveActivityType(soccer);
  }

  boolean isOrganizer(String username, ActivityType soccer) {
    User found = this.userDAO.findByUsername(username);
    return soccer.getOrganizers().contains(found);
  }

  // example ActivityType
  public static ActivityType soccer;
  
  @PostConstruct
  void init() {
    //soccer = this.baseData.getSoccer();
  }
}
