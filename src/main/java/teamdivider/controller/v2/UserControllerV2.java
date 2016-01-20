package teamdivider.controller.v2;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import teamdivider.bean.eo.Type;
import teamdivider.bean.eo.User;
import teamdivider.bean.vo.UserVO;
import teamdivider.dao.TypeDAO;
import teamdivider.dao.UserDAO;
import teamdivider.entity.EntityUtil;
import teamdivider.integration.QiniuIntegrationManager;
import teamdivider.util.FileUtil;
import teamdivider.util.PropertyUtil;

@RestController
@RequestMapping("/v2")
public class UserControllerV2 {

  @Autowired
  private UserDAO userDAO;

  @Autowired
  private TypeDAO typeDAO;

  @RequestMapping("/userLogin")
  public UserVO login(@RequestParam("username") String email) {
    User user = this.userDAO.findByEmail(email);
    if (user == null) return null;
    return new UserVO(user);
  }

  @RequestMapping("/user")
  public List<UserVO> user(
      @RequestParam(value = "username", defaultValue = "all") String email) {
    if ("all".equals(email)) {
      return EntityUtil.userVOsOf(this.userDAO.allUsers());
    } else {
      User user = this.userDAO.findByEmail(email);
      List<User> users = new ArrayList<User>();
      users.add(user);
      return EntityUtil.userVOsOf(users);
    }
  }

  @RequestMapping("/addUser")
  public UserVO addUser(@RequestParam("username") String username,
      @RequestParam("fullname") String fullname,
      @RequestParam("avator") String avator) { 
    return new UserVO(this.createUser(username, fullname, avator));
  }

  private User createUser(String email, String fullName, String avatar) {
    User existUser = this.userDAO.findByEmail(email);
    if (existUser != null) {
      return existUser;
    }
    User user = new User(email, fullName, avatar);
    this.userDAO.create(user);
    return user;
  }
  
  @RequestMapping("/addUserWithSubscribing")
  public UserVO addUserWithSubscribing(
      @RequestParam("username") String username,
      @RequestParam("fullname") String fullname,
      @RequestParam("avator") String avator,
      @RequestParam(value = "types", defaultValue = "") String types) {
    User user = this.createUser(username, fullname, avator);
    Set<String> typeSet = this.typesToSet(types);
    this.updateTypeSubscriberMapping(typeSet, user);
    UserVO vo = new UserVO(user);
    vo.setSubscribedTypes(typeSet);
    return vo;
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
  private void updateTypeSubscriberMapping(Set<String> typeSet, User user) {
    List<Type> activityTypes = this.typeDAO.getAllActivityTypes(true);
    for (Type type : activityTypes) {
      if (typeSet.contains(type.getName())) {
        this.typeDAO.userSubscribe(user.getUserId(), type.getTypeId(), user);
      } else {
        this.typeDAO.userUnSubscribe(user.getUserId(), type.getTypeId());
      }
    }
  }

  @RequestMapping("/deleteUser")
  public List<UserVO> deleteUser(@RequestParam("username") String username) {
    User user = this.userDAO.findByEmail(username);
    if (user == null)
      return user("all");
    new QiniuIntegrationManager()
        .deleteFile(PropertyUtil.AVATAR_BASE_LINK + user.getAvatar());
    this.userDAO.deleteUser(user.getUserId());
    return user("all");
  }

  @RequestMapping("/updateUserEmail")
  public User updateUserEmail(@RequestParam("username") String username,
      @RequestParam("email") String email) {
    User user = this.userDAO.findByEmail(username);
    user.setEmail(email);
    this.userDAO.saveUser(user);
    return user;
  }

  @RequestMapping("/updateUser")
  public User updateUser(@RequestParam("username") String username,
      @RequestParam("fullname") String fullname,
      @RequestParam(value = "types", defaultValue = "") String types) {
    User user = this.userDAO.findByEmail(username);
    user.setFullName(fullname);
    user = this.userDAO.saveUser(user);
    Set<String> typeSet = this.typesToSet(types);
    this.updateTypeSubscriberMapping(typeSet, user);
    return user;
  }

  @RequestMapping("/uploadHeadPicure")
  public User uploadHeadPicure(@RequestParam MultipartFile file,
      @RequestParam(value = "username") String username) throws IOException {
    String baseLink = PropertyUtil.AVATAR_BASE_LINK;
    String standardAvatarName = username + new Date().getTime() + ".jpg";
    // update avatar
    String newAvatarPath = baseLink + standardAvatarName;
    User user = this.userDAO.findByEmail(username);
    String formerAvatarPath = user.getAvatar();
    user.setAvatar(newAvatarPath);
    // make image size smaller than 50k
    double scale = file.getSize() > 50000 ? 50000 / file.getSize() : 1;
    scale = scale == 0 ? 0.2 : scale;
    // copy to server avatar path temperarily
    String tempImagePath = FileUtil.projectAvatarPath + standardAvatarName;
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
            .sourceRegion(Positions.CENTER, height, height).size(height, height)
            .toFile(imagePath);
      }
    } catch (IOException e) {
    }
  }
}
