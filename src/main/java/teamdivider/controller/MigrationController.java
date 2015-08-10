package teamdivider.controller;

import java.io.File;
import java.util.Date;
import java.util.List;

import net.coobird.thumbnailator.Thumbnails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import teamdivider.entity.ActivityEvent;
import teamdivider.entity.ActivityType;
import teamdivider.entity.User;
import teamdivider.integration.QiniuIntegrationManager;
import teamdivider.repo.ActivityTypeDAO;
import teamdivider.repo.UserDAO;
import teamdivider.util.FileUtil;
import teamdivider.util.PropertyUtil;

@RestController
public class MigrationController {

  @Autowired
  private UserDAO userDAO;
  
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
  
  @RequestMapping("/updateExistingUsersAvatarToQiniu")
  public List<User> updateExistingUsersAvatarToQiniu() {
    List<User> users = this.userDAO.allUsers();
    for (User user : users) {
      String qiniuAvatarBaseLink = PropertyUtil.AVATAR_BASE_LINK;
      String standardPictureName = user.getUsername() + new Date().getTime()
          + ".jpg";
      String formerAvatarPath = PropertyUtil.PROJECT_IMAGE_PATH
          + user.getAvatar();
      File formerAvatar = new File(formerAvatarPath);
      if (!formerAvatar.exists()) {
        continue;
      }
      // update avatar for user
      String newAvatar = qiniuAvatarBaseLink + standardPictureName;
      user.setAvatar(newAvatar);
      this.userDAO.saveUser(user);
      // upload generated jpg image to Qiniu cloud
      new QiniuIntegrationManager().uploadFile(formerAvatar,
          standardPictureName);
    }
    return users;
  }
}
