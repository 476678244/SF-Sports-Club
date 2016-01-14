package teamdivider.bean.eo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;
import org.mongodb.morphia.annotations.Transient;

@Entity
public class Type {

  @Id
  private ObjectId id;

  private long typeId;

  private String name;

  @Reference
  private Event latestEvent;

  @Transient
  private Set<User> organizers = new HashSet<User>();

  @Transient
  private Set<User> subscribers = new HashSet<User>();

  @Transient
  private Set<Event> events = new HashSet<Event>();

  @Transient
  private Map<Long, Integer> scores = new HashMap<Long, Integer>();

  private Type(ObjectId id, long typeId, String name, Event latestEvent,
      Set<User> organizers, Set<User> subscribers, Set<Event> events,
      Map<Long, Integer> scores) {
    super();
    this.id = id;
    this.typeId = typeId;
    this.name = name;
    this.latestEvent = latestEvent;
    this.organizers = organizers;
    this.subscribers = subscribers;
    this.events = events;
    this.scores = scores;
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

  public Map<Long, Integer> getScores() {
    return scores;
  }

  public void setScores(Map<Long, Integer> scores) {
    this.scores = scores;
  }

  public class Builder {

    private ObjectId id;

    private long typeId;

    private String name;

    private Event latestEvent;

    private Set<User> organizers = new HashSet<User>();

    private Set<User> subscribers = new HashSet<User>();

    private Set<Event> events = new HashSet<Event>();

    private Map<Long, Integer> scores = new HashMap<Long, Integer>();

    public Type build() {
      Type type = new Type(this.id, this.typeId, this.name, this.latestEvent,
          this.organizers, this.subscribers, this.events, this.scores);
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

    public Builder scores(Map<Long, Integer> scores) {
      this.scores = scores;
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
