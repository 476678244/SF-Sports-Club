/*
 * $Id$
 */
package teamdivider.bean.eo;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

import com.fasterxml.jackson.annotation.JsonIgnore;

import teamdivider.entity.User;

@Entity
public class Event {

  @JsonIgnore
  @Id
  ObjectId id;

  private long eventId;

  private String name;

  private Date startTime;

  private Date goTime;

  private String description;

  private long typeId;

  @Reference
  private Set<User> members = new HashSet<User>();

  // guest users
  private Set<String> guests = new HashSet<String>();

  public ObjectId getId() {
    return id;
  }

  public void setId(ObjectId id) {
    this.id = id;
  }

  public long getEventId() {
    return eventId;
  }

  public void setEventId(long eventId) {
    this.eventId = eventId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Date getStartTime() {
    return startTime;
  }

  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }

  public Date getGoTime() {
    return goTime;
  }

  public void setGoTime(Date goTime) {
    this.goTime = goTime;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public long getTypeId() {
    return typeId;
  }

  public void setTypeId(long typeId) {
    this.typeId = typeId;
  }

  public Set<User> getMembers() {
    return members;
  }

  public void setMembers(Set<User> members) {
    this.members = members;
  }

  public Set<String> getGuests() {
    return guests;
  }

  public void setGuests(Set<String> guests) {
    this.guests = guests;
  }

}
