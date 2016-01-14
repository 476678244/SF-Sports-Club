/*
 * $Id$
 */
package teamdivider.bean.vo;

import java.util.ArrayList;
import java.util.List;

import teamdivider.entity.ActivityEvent;
import teamdivider.entity.ActivityType;
import teamdivider.entity.EntityUtil;

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

  public String getName() {
    return this.name;
  }
  
  public List<ActivityEvent> getEvents() {
    List<ActivityEvent> activityEvents = new ArrayList<ActivityEvent>(
        events.size());
    for (EventVO eventVO : this.events) {
      activityEvents.add(eventVO);
    }
    return activityEvents;
  }

  public List<teamdivider.entity.User> getOrganizers() {
    return EntityUtil.userVOsToUsers(organizers);
  }

  public List<teamdivider.entity.User> getSubscribers() {
    return EntityUtil.userVOsToUsers(subscribers);
  }

  public ActivityEvent getLatestEvent() {
    return this.latestEvent;
  }
  
  public int getLatestOrdinal() {
    return this.latestEvent.getOrdinal();
  }
}
