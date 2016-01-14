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

import teamdivider.entity.User;

@Entity
public class Event {

  @Id
  ObjectId id;

  private long eventId;

  private String name;

  private Date startTime;

  private Date goTime;

  private String description;

  private long typeId;

  @Reference(lazy = true)
  private Set<User> members = new HashSet<User>();

  // guest users
  private Set<String> guests = new HashSet<String>();

  private Event(ObjectId id, long eventId, String name, Date startTime,
      Date goTime, String description, long typeId, Set<User> members,
      Set<String> guests) {
    super();
    this.id = id;
    this.eventId = eventId;
    this.name = name;
    this.startTime = startTime;
    this.goTime = goTime;
    this.description = description;
    this.typeId = typeId;
    this.members = members;
    this.guests = guests;
  }

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

  public class Builder {

    ObjectId id;

    private long eventId;

    private String name;

    private Date startTime;

    private Date goTime;

    private String description;

    private long typeId;

    private Set<User> members = new HashSet<User>();

    // guest users
    private Set<String> guests = new HashSet<String>();

    public Event build() {
      Event event = new Event(this.id, this.eventId, this.name, this.startTime,
          this.goTime, this.description, this.typeId, this.members,
          this.guests);
      return event;
    }

    public Builder id(ObjectId id) {
      this.id = id;
      return this;
    }

    public Builder eventId(long eventId) {
      this.eventId = eventId;
      return this;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder startTime(Date startTime) {
      this.startTime = startTime;
      return this;
    }

    public Builder goTime(Date goTime) {
      this.goTime = goTime;
      return this;
    }

    public Builder description(String description) {
      this.description = description;
      return this;
    }

    public Builder typeId(long typeId) {
      this.typeId = typeId;
      return this;
    }

    public Builder members(Set<User> members) {
      this.members = members;
      return this;
    }

    public Builder guests(Set<String> guests) {
      this.guests = guests;
      return this;
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (eventId ^ (eventId >>> 32));
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
    Event other = (Event) obj;
    if (eventId != other.eventId)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Event [eventId=" + eventId + ", name=" + name + ", startTime="
        + startTime + ", goTime=" + goTime + ", description=" + description
        + ", typeId=" + typeId + "]";
  }

}
