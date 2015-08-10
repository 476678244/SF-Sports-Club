package teamdivider.entity;

import java.util.HashSet;
import java.util.Set;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import teamdivider.util.ContextUtil;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class User {
  @JsonIgnore@Id ObjectId id;
  String username;
  String password;
  String fullname;
  String avatar;
  // this is the temp score to be used in Fendui
  int score;
  
  User() {}
  
  public User(String username, String fullname, String avatar) {
    this(username, "123", fullname, avatar, 0);
  }
  
  public User(String username, String fullname, String avatar, int score) {
    this(username, "123", fullname, avatar, score);
  }
  
  public User(String username, String password, String fullname, String avatar, int score) {
    this.username = username;
    this.password = password;
    this.fullname = fullname;
    this.avatar = avatar;
    this.score = score;
  }

  public ObjectId getId() {
    return id;
  }
  void setId(ObjectId id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }
  
  public String getFullname() {
    return fullname;
  }
  
  public String getAvatar() {
    return avatar;
  }
  
  public int getScore() {
    return score;
  }

  public int getSimpleScore() {
    return EntityUtil.simpleScore(score);
  }

  public void setScore(int score) {
	this.score = score;
  }

  @Override
  public boolean equals(Object obj) {
    if (false == obj instanceof User) {
      return false;
    }
    User other = (User) obj;
    return other.getId().equals(getId());
  }
  
  @Override
  public int hashCode() {
    return this.username.hashCode();
  }
  
  @Override
  public String toString() {
    return username + " " + id;
  }

  public void setUsername(String username) {
    this.username = username;
  }
  
  public Set<String> getSubscribedTypes() {
    Set<String> types = new HashSet<String>();
    for (ActivityType type : ContextUtil.ACTIVITY_TYPE_DAO
        .getAllActivityTypes(true)) {
      if (type.hasSubscriber(this.username)) {
        types.add(type.getName());
      }
    }
    return types;
  }

  public void setFullname(String fullname) {
    this.fullname = fullname;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }
}
