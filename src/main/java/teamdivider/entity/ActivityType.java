package teamdivider.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;
import org.mongodb.morphia.annotations.Transient;

@Entity
public class ActivityType {
  @Id
  protected ObjectId id;
  protected String name;
  protected String regularTime = "";
  protected int latestOrdinal = 0;
  @Reference
  protected List<User> organizers = new ArrayList<User>();
  @Reference
  protected List<User> subscribers = new ArrayList<User>();
  protected List<ActivityEvent> events = new ArrayList<ActivityEvent>();
  // username/score map
  @Transient
  protected Map<String, Integer> usersScore = new HashMap<String, Integer>();
  protected Map<ObjectId, Integer> scoreStatistic = new HashMap<ObjectId, Integer>();

  public ActivityType() {
  }

  public ActivityType(String name, User organizer) {
    this.name = name;
    this.getOrganizers().add(organizer);
  }

  public ObjectId getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getRegularTime() {
    return regularTime;
  }

  public void setRegularTime(String regularTime) {
    this.regularTime = regularTime;
  }

  public int getLatestOrdinal() {
    return latestOrdinal;
  }

  public void setLatestOrdinal(int latestOrdinal) {
    this.latestOrdinal = latestOrdinal;
  }

  public List<User> getOrganizers() {
    return organizers;
  }

  public void setOrganizers(List<User> organizers) {
    this.organizers = organizers;
  }

  /**
   * Don't change the returned list!
   * 
   * @return events, but avoid change it!
   */
  public List<ActivityEvent> getEvents() {
    return events;
  }

  public void setEvents(List<ActivityEvent> events) {
    this.events = events;
  }

  public void addEvent(ActivityEvent event) {
    events.add(event);
    latestOrdinal++;
  }

  public ActivityEvent getLatestEvent() {
    for (ActivityEvent event : this.events) {
      if (event.getOrdinal() == this.latestOrdinal) {
        return event;
      }
    }
    return null;
  }

  public ActivityEvent getEvent(int ordinal) {
    for (ActivityEvent event : events) {
      if (event.getOrdinal() == ordinal) {
        return event;
      }
    }
    return null;
  }

  @Override
  public boolean equals(Object obj) {
    if (false == obj instanceof User) {
      return false;
    }
    ActivityType other = (ActivityType) obj;
    return other.getId().equals(getId());
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  public void loadUsersScore(List<User> users) {
    for (User user : users) {
      this.usersScore.put(user.getUsername(),
          this.scoreStatistic.get(user.getId()));
    }
  }

  public int getUserScore(String username) {
    return this.usersScore.get(username) == null ? 0 : this.usersScore
        .get(username);
  }

  public void updateUserScore(ObjectId userId, int score) {
    this.scoreStatistic.put(userId, score);
  }

  public void setScoreStatistic(Map<ObjectId, Integer> scoreStatistic) {
    this.scoreStatistic = scoreStatistic;
  }

  public Map<ObjectId, Integer> getScoreStatistic() {
    return scoreStatistic;
  }

  public ActivityEvent getEventByOrdinal(int ordinal) {
    for (ActivityEvent event : this.getEvents()) {
      if (ordinal == event.getOrdinal()) {
        return event;
      }
    }
    return null;
  }

  public void removeUserScore(String username) {
    this.scoreStatistic.remove(username);
  }

  public void removeUserFromType(User user) {
    for (ActivityEvent event : this.getEvents()) {
      event.removeMember(user);
    }
    EntityUtil.removeUserFromList(organizers, user);
    EntityUtil.removeUserFromList(subscribers, user);
    removeUserScore(user.getUsername());
  }

  public List<User> getSubscribers() {
    return subscribers;
  }

  public void setSubscribers(List<User> subscribers) {
    this.subscribers = subscribers;
  }
  
  public void userSubscribe(User user) {
    if (!this.hasSubscriber(user.getUsername()))
      this.subscribers.add(user);
  }
  
  public boolean hasSubscriber(String username) {
    for (User user : this.subscribers) {
      if (user.getUsername().equals(username)) {
        return true;
      }
    }
    return false;
  }
  
  public void unSubscribe(String username) {
    for (User user : this.subscribers) {
      if (user.getUsername().equals(username)) {
        this.subscribers.remove(user);
        return ;
      }
    }
  }
  
  public ActivityType simpleType() {
    this.scoreStatistic.clear();
    this.usersScore.clear();
    for (ActivityEvent event : this.events) {
      event.simpleEvent();
    }
    return this;
  }
  
  public ActivityEvent hasEvent(ActivityEvent event) {
    for (ActivityEvent each : this.events) {
      if (each.equals(event)) {
        return each;
      }
    }
    return null;
  }

}
