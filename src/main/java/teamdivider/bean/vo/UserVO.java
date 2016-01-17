/*
 * $Id$
 */
package teamdivider.bean.vo;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.mongodb.morphia.annotations.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import teamdivider.bean.eo.Type;
import teamdivider.bean.eo.User;
import teamdivider.util.ContextUtil;

public class UserVO {

  private long userId;

  private String email;

  private String fullName;
  
  private String avatar;

  @Transient
  private Set<Type> userSubscribedTypes = new HashSet<Type>();

  public UserVO(User eo) {
    this.userId = eo.getUserId();
    this.email = eo.getEmail();
    this.fullName = eo.getFullName();
    this.avatar = eo.getAvatar();
    this.userSubscribedTypes = eo.getSubscribedTypes();
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

  @JsonIgnore
  public Set<Type> getUserSubscribedTypes() {
    return userSubscribedTypes;
  }

  public void setUserSubscribedTypes(Set<Type> userSubscribedTypes) {
    this.userSubscribedTypes = userSubscribedTypes;
  }

  public Set<String> getSubscribedTypes() {
    if (!ContextUtil.getContext().fetchUserSubscribedTypes)
      return Collections.emptySet();
    Set<String> types = new HashSet<String>();
    for (Type type : this.userSubscribedTypes) {
      types.add(type.getName());
    }
    return types;
  }

  public String getUsername() {
    return this.email;
  }
}
