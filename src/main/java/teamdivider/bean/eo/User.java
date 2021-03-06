/*
 * $Id$
 */
package teamdivider.bean.eo;

import java.util.HashSet;
import java.util.Set;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Transient;

@Entity("UserV2")
public class User {

  @Id
  private ObjectId id;

  private long userId;
  
  private String email;

  private String fullName;
  
  private String avatar;
  
  @Transient
  private Set<Type> subscribedTypes = new HashSet<Type>();

  public User() {  
  }
  
  public User(String email, String fullName, String avatar) {
    this.email = email;
    this.fullName = fullName;
    this.avatar = avatar;
  }

  public Set<Type> getSubscribedTypes() {
    return subscribedTypes;
  }

  public void setSubscribedTypes(Set<Type> subscribedTypes) {
    this.subscribedTypes = subscribedTypes;
  }

  public ObjectId getId() {
    return id;
  }

  public void setId(ObjectId id) {
    this.id = id;
  }

  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getFullName() {
    return fullName;
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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (userId ^ (userId >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    User other = (User) obj;
    if (userId != other.userId)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "User [userId=" + userId + ", email=" + email + ", avatar=" + avatar
        + "]";
  }

}
