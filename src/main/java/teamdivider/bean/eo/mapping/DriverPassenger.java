/*
 * $Id$
 */
package teamdivider.bean.eo.mapping;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity
public class DriverPassenger {

  @Id
  private ObjectId id;

  private long mappingId;

  private long eventId;

  private long driverId;

  private long passengerId;

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

  public long getDriverId() {
    return driverId;
  }

  public void setDriverId(long driverId) {
    this.driverId = driverId;
  }

  public long getPassengerId() {
    return passengerId;
  }

  public void setPassengerId(long passengerId) {
    this.passengerId = passengerId;
  }
  
}
