/*
 * $Id$
 */
package teamdivider.bean.eo;

import java.util.HashSet;
import java.util.Set;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

@Entity
public class UserMapping {

  @Id
  private ObjectId id;
  
  private long userId;
  
  @Reference(lazy = true)
  private User user;
  
  @Reference(lazy = true)
  private Set<Type> subscribedTypes = new HashSet<Type>();

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

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Set<Type> getSubscribedTypes() {
    return subscribedTypes;
  }

  public void setSubscribedTypes(Set<Type> subscribedTypes) {
    this.subscribedTypes = subscribedTypes;
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
    UserMapping other = (UserMapping) obj;
    if (userId != other.userId)
      return false;
    return true;
  }
  
}
