/*
 * $Id$
 */
package teamdivider.bean.eo.mapping;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity
public class TypeUserScore {

  @Id
  private ObjectId id;
  
  private long mappingId;
  
  private long userId;
  
  private long typeId;
  
  private int score;

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

  public long getTypeId() {
    return typeId;
  }

  public void setTypeId(long typeId) {
    this.typeId = typeId;
  }

  public int getScore() {
    return score;
  }

  public void setScore(int score) {
    this.score = score;
  }

}
