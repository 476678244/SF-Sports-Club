/*
 * $Id$
 */
package teamdivider.bean.eo;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Transient;

import teamdivider.entity.Team;

@Entity
public class Event {

  @Id
  private ObjectId id;

  private long eventId;

  private String name;

  private Date startTime;

  private Date goTime;

  private String description;
  
  private long typeId;

  @Transient
  private Set<User> members = new HashSet<User>();

  @Transient
  private Set<User> drivers = new HashSet<User>();

  @Transient
  private Map<Long, Set<Long>> passengers = new HashMap<Long, Set<Long>>();

  @Transient
  private Map<Integer, List<Team>> fenDuiResult = new HashMap<Integer, List<Team>>();

  // guest users
  private Set<String> guests = new HashSet<String>();

  private Event(ObjectId id, long eventId, String name, Date startTime,
      Date goTime, String description, Set<User> members, Set<String> guests,
      Set<User> drivers, Map<Long, Set<Long>> passengers,
      Map<Integer, List<Team>> fenDuiResult, long typeId) {
    super();
    this.id = id;
    this.eventId = eventId;
    this.name = name;
    this.startTime = startTime;
    this.goTime = goTime;
    this.description = description;
    this.members = members;
    this.guests = guests;
    this.drivers = drivers;
    this.passengers = passengers;
    this.fenDuiResult = fenDuiResult;
    this.typeId = typeId;
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

  public long getTypeId() {
    return typeId;
  }

  public void setTypeId(long typeId) {
    this.typeId = typeId;
  }

  public Set<User> getDrivers() {
    return drivers;
  }

  public void setDrivers(Set<User> drivers) {
    this.drivers = drivers;
  }

  public Map<Long, Set<Long>> getPassengers() {
    return passengers;
  }

  public void setPassengers(Map<Long, Set<Long>> passengers) {
    this.passengers = passengers;
  }

  public Map<Integer, List<Team>> getFenDuiResult() {
    return fenDuiResult;
  }

  public void setFenDuiResult(Map<Integer, List<Team>> fenDuiResult) {
    this.fenDuiResult = fenDuiResult;
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

    private Set<User> drivers = new HashSet<User>();

    private Map<Long, Set<Long>> passengers = new HashMap<Long, Set<Long>>();

    private Map<Integer, List<Team>> fenDuiResult = new HashMap<Integer, List<Team>>();

    public Event build() {
      Event event = new Event(this.id, this.eventId, this.name, this.startTime,
          this.goTime, this.description, this.members, this.guests, drivers,
          passengers, fenDuiResult, this.typeId);
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

    public Builder drivers(Set<User> drivers) {
      this.drivers = drivers;
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
        + startTime + ", goTime=" + goTime + ", description=" + description;
  }

}
