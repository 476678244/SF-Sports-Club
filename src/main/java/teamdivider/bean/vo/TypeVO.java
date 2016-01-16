/*
 * $Id$
 */
package teamdivider.bean.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import teamdivider.bean.eo.Event;
import teamdivider.bean.eo.Type;
import teamdivider.bean.eo.User;

public class TypeVO {

  private long typeId;

  private String name;

  private EventVO latestEvent;

  private List<UserVO> organizers = new ArrayList<UserVO>();

  private List<UserVO> subscribers = new ArrayList<UserVO>();

  private List<EventVO> events = new ArrayList<EventVO>();

  private Map<Long, Integer> scores = new HashMap<Long, Integer>();

  public TypeVO(Type eo) {
    this.typeId = eo.getTypeId();
    this.name = eo.getName();
    this.latestEvent = new EventVO(eo.getLatestEvent());
    Set<User> organizerEOs = eo.getOrganizers();
    if (organizers != null) {
      for (User user : organizerEOs) {
        this.organizers.add(new UserVO(user));
      }
    }
    Set<User> subscriberEOs = eo.getSubscribers();
    if (subscriberEOs != null) {
      for (User user : subscriberEOs) {
        this.subscribers.add(new UserVO(user));
      }
    }
    Set<Event> eventEOs = eo.getEvents();
    if (eventEOs != null) {
      for (Event event : eventEOs) {
        this.events.add(new EventVO(event));
      }
    }
    this.scores = eo.getScores();
  }

  public long getTypeId() {
    return typeId;
  }

  public void setTypeId(long typeId) {
    this.typeId = typeId;
  }

  public String getName() {
    return this.name;
  }

  @JsonIgnore
  public Map<Long, Integer> getScores() {
    return scores;
  }

  public void setScores(Map<Long, Integer> scores) {
    this.scores = scores;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setLatestEvent(EventVO latestEvent) {
    this.latestEvent = latestEvent;
  }

  public void setOrganizers(List<UserVO> organizers) {
    this.organizers = organizers;
  }

  public void setSubscribers(List<UserVO> subscribers) {
    this.subscribers = subscribers;
  }

  public void setEvents(List<EventVO> events) {
    this.events = events;
  }

  public List<EventVO> getEvents() {
    return new ArrayList<EventVO>(this.events);
  }

  public List<UserVO> getOrganizers() {
    return new ArrayList<UserVO>(this.organizers);
  }

  public List<UserVO> getSubscribers() {
    return new ArrayList<UserVO>(this.subscribers);
  }

  public EventVO getLatestEvent() {
    return this.latestEvent;
  }

  public int getLatestOrdinal() {
    return this.latestEvent.getOrdinal();
  }
}
