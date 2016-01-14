/*
 * $Id$
 */
package teamdivider.bean.vo;

import java.util.ArrayList;
import java.util.List;

import teamdivider.entity.ActivityEvent;
import teamdivider.entity.ActivityType;
import teamdivider.entity.User;

public class TypeVO extends ActivityType {

  private long typeId;

  private EventVO latestEvent;

  private List<EventVO> events = new ArrayList<EventVO>();

  private List<UserVO> organizers = new ArrayList<UserVO>();

  private List<UserVO> subscribers = new ArrayList<UserVO>();

  public long getTypeId() {
    return typeId;
  }

  public void setTypeId(long typeId) {
    this.typeId = typeId;
  }

  public List<ActivityEvent> getEvents() {
    List<ActivityEvent> activityEvents = new ArrayList<ActivityEvent>(
        events.size());
    for (EventVO eventVO : this.events) {
      activityEvents.add(eventVO);
    }
    return activityEvents;
  }

  public List<User> getOrganizers() {
    List<User> users = new ArrayList<User>(this.organizers.size());
    for (UserVO userVO : this.organizers) {
      users.add(userVO);
    }
    return users;
  }

  public List<User> getSubscribers() {
    List<User> users = new ArrayList<User>(this.subscribers.size());
    for (UserVO userVO : this.subscribers) {
      users.add(userVO);
    }
    return users;
  }

  public ActivityEvent getLatestEvent() {
    return this.latestEvent;
  }
}
