/*
 * $Id$
 */
package teamdivider.bean.eo.mapping;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import teamdivider.bean.eo.Event;

@Entity
public class TypeEvent {

  @Id
  private ObjectId id;
  
  private long mappingId;
  
  private long typeId;
  
  private long eventId;
  
  private Event event;

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

  public long getTypeId() {
    return typeId;
  }

  public void setTypeId(long typeId) {
    this.typeId = typeId;
  }

  public long getEventId() {
    return eventId;
  }

  public void setEventId(long eventId) {
    this.eventId = eventId;
  }

  public Event getEvent() {
    return event;
  }

  public void setEvent(Event event) {
    this.event = event;
  }
  
}
