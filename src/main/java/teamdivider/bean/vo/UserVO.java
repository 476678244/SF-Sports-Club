/*
 * $Id$
 */
package teamdivider.bean.vo;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import teamdivider.bean.eo.Type;
import teamdivider.bean.eo.User;

public class UserVO {

  private long userId;

  private String email;

  private String fullName;
  
  private String avatar;

  private Set<String> subscribedTypes = new HashSet<String>();

  public UserVO(User eo) {
    this.userId = eo.getUserId();
    this.email = eo.getEmail();
    this.fullName = eo.getFullName();
    this.avatar = eo.getAvatar();
    Set<Type> typeEOs = eo.getSubscribedTypes();
    if (typeEOs != null) {
      for (Type type : typeEOs) {
        this.subscribedTypes.add(type.getName());
      }
    }
  }
  
  @JsonIgnore
  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

  @JsonIgnore
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @JsonIgnore
  public String getFullName() {
    return fullName;
  }

  public String getFullname() {
    return this.fullName;
  }
  
  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }

  public Set<String> getSubscribedTypes() {
    return this.getSubscribedTypes();
  }

  public String getUsername() {
    return this.email;
  }

  public void setSubscribedTypes(Set<String> subscribedTypes) {
    this.subscribedTypes = subscribedTypes;
  }
}
