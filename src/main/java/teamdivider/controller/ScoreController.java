package teamdivider.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import teamdivider.entity.ActivityType;
import teamdivider.entity.User;
import teamdivider.repo.ActivityTypeDAO;
import teamdivider.repo.BaseData;
import teamdivider.repo.UserDAO;

@RestController
public class ScoreController {

  @Autowired
  private BaseData baseData;

  @Autowired
  private UserDAO userDAO;

  @Autowired
  private ActivityTypeDAO activityTypeDAO;

  private static ActivityType soccer = null;

  @RequestMapping("/scoreStatistic")
  public Map<String, Integer> scoreStatistic(
      @RequestParam(value = "activityType", defaultValue = "soccer") String activityType) {
    Map<ObjectId, Integer> scoreStatistic = this.activityTypeDAO
        .getActivityTypeByName(activityType).getScoreStatistic();
    return this.readableStatistic(scoreStatistic);
  }

  private Map<String, Integer> readableStatistic(
      Map<ObjectId, Integer> scoreStatistic) {
    Map<String, Integer> readableStatistic = new HashMap<String, Integer>();
    Set<ObjectId> ids = scoreStatistic.keySet();
    for (ObjectId id : ids) {
      User user = this.userDAO.getUser(id);
      if (user == null) {
        continue;
      }
      readableStatistic.put(user.getUsername(), scoreStatistic.get(id));
    }
    return readableStatistic;
  }

  @RequestMapping("/installRefreshScoreStatistic")
  public Map<String, Integer> installRefreshScoreStatistic() {
    soccer = this.baseData.getSoccer();
    Map<ObjectId, Integer> effectiveStatistic = BaseData
        .getActivitiesScoreStatistic(this.userDAO);
    for (ObjectId userId : effectiveStatistic.keySet()) {
      soccer.updateUserScore(userId, effectiveStatistic.get(userId));
    }
    soccer = this.activityTypeDAO.saveActivityType(soccer);
    return this.readableStatistic(soccer.getScoreStatistic());
  }

  @RequestMapping("/installScoreStatistic")
  public Map<String, Integer> installScoreStatistic() {
    soccer = this.baseData.getSoccer();
    Map<ObjectId, Integer> effectiveStatistic = BaseData
        .getActivitiesScoreStatistic(this.userDAO);
    for (ObjectId userId : effectiveStatistic.keySet()) {
      if (!soccer.getScoreStatistic().containsKey(userId)) {
        soccer.updateUserScore(userId, effectiveStatistic.get(userId));
      }
    }
    soccer = this.activityTypeDAO.saveActivityType(soccer);
    return this.readableStatistic(soccer.getScoreStatistic());
  }

  @RequestMapping("/updateUserScore")
  public Map<String, Integer> updateUserScore(
      @RequestParam(value = "username") String username,
      @RequestParam(value = "activityType", defaultValue = "soccer") String activityType,
      @RequestParam(value = "score", defaultValue = "0") int score) {
    ActivityType type = this.activityTypeDAO
        .getActivityTypeByName(activityType);
    type.updateUserScore(this.userDAO.findByUsername(username).getId(), score);
    soccer = this.baseData.saveExampleActivityType(type);
    return this.readableStatistic(soccer.getScoreStatistic());
  }
}
