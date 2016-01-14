package teamdivider.bean.eo;

import java.util.HashSet;
import java.util.Set;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

import teamdivider.entity.User;

@Entity
public class Type {

  @Id
  private ObjectId id;

  private long typeId;

  private String name;

  @Reference
  private Event latestEvent;

  @Reference
  private Set<User> organizers = new HashSet<User>();

  @Reference(lazy = true)
  private Set<User> subscribers = new HashSet<User>();

  @Reference(lazy = true)
  private Set<Event> events = new HashSet<Event>();

  private Type(ObjectId id, long typeId, String name, Event latestEvent,
      Set<User> organizers, Set<User> subscribers, Set<Event> events) {
    super();
    this.id = id;
    this.typeId = typeId;
    this.name = name;
    this.latestEvent = latestEvent;
    this.organizers = organizers;
    this.subscribers = subscribers;
    this.events = events;
  }

  public ObjectId getId() {
    return id;
  }

  public void setId(ObjectId id) {
    this.id = id;
  }

  public long getTypeId() {
    return typeId;
  }

  public void setTypeId(long typeId) {
    this.typeId = typeId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Event getLatestEvent() {
    return latestEvent;
  }

  public void setLatestEvent(Event latestEvent) {
    this.latestEvent = latestEvent;
  }

  public Set<User> getOrganizers() {
    return organizers;
  }

  public void setOrganizers(Set<User> organizers) {
    this.organizers = organizers;
  }

  public Set<User> getSubscribers() {
    return subscribers;
  }

  public void setSubscribers(Set<User> subscribers) {
    this.subscribers = subscribers;
  }

  public Set<Event> getEvents() {
    return events;
  }

  public void setEvents(Set<Event> events) {
    this.events = events;
  }

  public class Builder {

    private ObjectId id;

    private long typeId;

    private String name;

    private Event latestEvent;

    private Set<User> organizers = new HashSet<User>();

    private Set<User> subscribers = new HashSet<User>();

    private Set<Event> events = new HashSet<Event>();

    public Type build() {
      Type type = new Type(this.id, this.typeId, this.name, this.latestEvent,
          this.organizers, this.subscribers, this.events);
      return type;
    }

    public Builder id(ObjectId id) {
      this.id = id;
      return this;
    }

    public Builder typeId(long typeId) {
      this.typeId = typeId;
      return this;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder latestEvent(Event latestEvent) {
      this.latestEvent = latestEvent;
      return this;
    }

    public Builder organizers(Set<User> organizers) {
      this.organizers = organizers;
      return this;
    }

    public Builder subscribers(Set<User> subscribers) {
      this.subscribers = subscribers;
      return this;
    }

    public Builder events(Set<Event> events) {
      this.events = events;
      return this;
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (typeId ^ (typeId >>> 32));
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
    Type other = (Type) obj;
    if (typeId != other.typeId)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Type [typeId=" + typeId + ", name=" + name + ", latestEvent="
        + latestEvent + "]";
  }
  
}
