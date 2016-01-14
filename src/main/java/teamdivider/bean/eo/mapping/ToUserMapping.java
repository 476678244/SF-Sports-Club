/*
 * $Id$
 */
package teamdivider.bean.eo.mapping;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

import teamdivider.bean.eo.User;

public abstract class ToUserMapping {

  @Id
  private ObjectId id;
  
  private long mappingId;
  
  private long userId;
  
  @Reference
  private User user;

  public ObjectId getId() {
    return id;
  }

  public void setId(ObjectId id) {
    this.id = id;
  }

  public long getMappingId() {
    return mappingId;
  }

  public void setMappingId(long mappingId) {
    this.mappingId = mappingId;
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
}
