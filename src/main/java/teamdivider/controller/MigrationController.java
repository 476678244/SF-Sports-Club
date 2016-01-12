package teamdivider.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import teamdivider.entity.ActivityEvent;
import teamdivider.entity.ActivityType;
import teamdivider.repo.ActivityTypeDAO;

@RestController
public class MigrationController {
  
  @Autowired
  private ActivityTypeDAO activityTypeDAO;
  
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
}
