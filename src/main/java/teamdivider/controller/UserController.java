package teamdivider.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Position;
import net.coobird.thumbnailator.geometry.Positions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import teamdivider.entity.ActivityType;
import teamdivider.entity.User;
import teamdivider.integration.QiniuIntegrationManager;
import teamdivider.repo.ActivityTypeDAO;
import teamdivider.repo.UserDAO;
import teamdivider.util.FileUtil;
import teamdivider.util.PropertyUtil;

@RestController
public class UserController {

  @Autowired
  private UserDAO userDAO;
  
  @Autowired
  private ActivityTypeDAO activityTypeDAO;
  
  @RequestMapping("/userLogin")
  public User login(@RequestParam("username") String username) {
    User user = this.userDAO.findByUsername(username);
    return user;
  }
  
  @RequestMapping("/user")
  public List<User> user(
      @RequestParam(value = "username", defaultValue = "all") String username) {
    if ("all".equals(username)) {
      return this.userDAO.allUsers();
    } else {
      User user = this.userDAO.findByUsername(username);
      List<User> users = new ArrayList<User>();
      users.add(user);
      return users;
    }
  }
  
  @RequestMapping("/addUser")
  public User addUser(@RequestParam("username") String username,
      @RequestParam("fullname") String fullname,
      @RequestParam("avator") String avator) {
    User existUser = this.userDAO.findByUsername(username);
    if ( existUser != null) {
      return existUser;
    } 
    User user = new User(username, fullname, avator);
    return this.userDAO.saveUser(user);
  }
  
  @RequestMapping("/addUserWithSubscribing")
  public User addUserWithSubscribing(@RequestParam("username") String username,
      @RequestParam("fullname") String fullname,
      @RequestParam("avator") String avator,
      @RequestParam(value = "types", defaultValue = "") String types) {
    User user = this.addUser(username, fullname, avator);
    Set<String> typeSet = this.typesToSet(types);
    this.subscribeUserToTypes(typeSet, user);
    return user;
  }
  
  private Set<String> typesToSet(String types) {
    StringTokenizer tokenizer = new StringTokenizer(types);
    Set<String> typeSet = new HashSet<String>();
    while (tokenizer.hasMoreElements()) {
      typeSet.add((String) tokenizer.nextToken("/"));
    }
    return typeSet;
  }

  // full replace
  private void subscribeUserToTypes(Set<String> typeSet, User user) {
    List<ActivityType> activityTypes = this.activityTypeDAO
        .getAllActivityTypes();
    for (ActivityType type : activityTypes) {
      if (typeSet.contains(type.getName())) {
        type.userSubscribe(user);
      } else {
        type.unSubscribe(user.getUsername());
      }
      this.activityTypeDAO.saveActivityType(type);
    }
  }

  @RequestMapping("/deleteUser")
  public List<User> deleteUser(@RequestParam("username") String username) {
    User user = this.userDAO.findByUsername(username);
    for (ActivityType type : this.activityTypeDAO.getAllActivityTypes()) {
      type.removeUserFromType(user);
      this.activityTypeDAO.saveActivityType(type);
    }
    new QiniuIntegrationManager().deleteFile(PropertyUtil.AVATAR_BASE_LINK
        + user.getAvatar());
    this.userDAO.deleteUser(username);
    return user("all");
  }
  
  @RequestMapping("/updateUserEmail")
  public User updateUserEmail(@RequestParam("username") String username,
      @RequestParam("email") String email) {
    User user = this.userDAO.findByUsername(username);
    user.setUsername(email);
    user = this.userDAO.saveUser(user);
    return user;
  }
  
  @RequestMapping("/updateUser")
  public User updateUser(@RequestParam("username") String username,
      @RequestParam("fullname") String fullname,
      @RequestParam(value = "types", defaultValue = "") String types) {
    User user = this.userDAO.findByUsername(username);
    user.setFullname(fullname);
    user = this.userDAO.saveUser(user);
    Set<String> typeSet = this.typesToSet(types);
    this.subscribeUserToTypes(typeSet, user);
    return user;
  }
  
  @RequestMapping("/uploadHeadPicure")
  public User uploadHeadPicure(@RequestParam MultipartFile file,
      @RequestParam(value = "username") String username) throws IOException {
    String baseLink = PropertyUtil.AVATAR_BASE_LINK;
    String standardAvatarName = username + new Date().getTime() + ".jpg";
    // update avatar
    String newAvatarPath = baseLink + standardAvatarName;
    User user = this.userDAO.findByUsername(username);
    String formerAvatarPath = user.getAvatar();
    user.setAvatar(newAvatarPath);
    // make image size smaller than 50k
    double scale = file.getSize() > 50000 ? 50000 / file.getSize() : 1;
    scale = scale == 0 ? 0.2 : scale;
    // copy to server avatar path temperarily
    String tempImagePath = FileUtil.projectHeadPicturePath + standardAvatarName;
    Thumbnails.of(file.getInputStream()).outputFormat("jpg").outputQuality(1)
        .scale(scale).toFile(tempImagePath);
    imageToSquare(tempImagePath);
    // upload generated jpg image to Qiniu cloud
    new QiniuIntegrationManager().uploadFileToQiniu(new File(tempImagePath),
        standardAvatarName,
        formerAvatarPath.replaceAll(PropertyUtil.AVATAR_BASE_LINK, ""));
    this.userDAO.saveUser(user);
    new File(tempImagePath).delete();
    return user;
  }
  
  private void imageToSquare(String imagePath) {
    try {
      BufferedImage image = ImageIO.read(new File(imagePath));
      int width = image.getWidth();
      int height = image.getHeight();
      if (height < width) {
        Thumbnails.of(new File(imagePath))
            .sourceRegion(Positions.CENTER, height, height)
            .size(height, height).toFile(imagePath);
      }
    } catch (IOException e) {
    }
  }

  @PostConstruct
  void init() throws FileNotFoundException {
  }
}
