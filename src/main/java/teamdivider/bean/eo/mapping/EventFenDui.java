/*
 * $Id$
 */
package teamdivider.bean.eo.mapping;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity
public class EventFenDui {

  @Id
  private ObjectId id;

  private long mappingId;
  
  private long eventId;
  
  private long fenDuiId;

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

  public long getEventId() {
    return eventId;
  }

  public void setEventId(long eventId) {
    this.eventId = eventId;
  }

  public long getFenDuiId() {
    return fenDuiId;
  }

  public void setFenDuiId(long fenDuiId) {
    this.fenDuiId = fenDuiId;
  }

}
