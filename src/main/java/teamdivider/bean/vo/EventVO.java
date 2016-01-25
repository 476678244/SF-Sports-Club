/*
 * $Id$
 */
package teamdivider.bean.vo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mongodb.morphia.annotations.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import teamdivider.bean.eo.Event;
import teamdivider.bean.eo.Type;
import teamdivider.bean.eo.User;
import teamdivider.entity.EntityUtil;
import teamdivider.entity.Team;
import teamdivider.util.ContextUtil;

public class EventVO {

  private long eventId;

  private String name;

  private Date startTime;

  private Date goTime;

  private String description;

  private long typeId;

  @Transient
  private Set<UserVO> members = new HashSet<UserVO>();

  @Transient
  private Set<UserVO> drivers = new HashSet<UserVO>();

  @Transient
  private Map<Long, Set<Long>> passengers = new HashMap<Long, Set<Long>>();

  @Transient
  private Map<Integer, List<Team>> fenDuiResult = new HashMap<Integer, List<Team>>();

  // guest users
  private Set<String> guests = new HashSet<String>();

  public EventVO(Event event) {
    this.description = event.getDescription();
    for (User user : event.getDrivers()) {
      this.drivers.add(new UserVO(user));
    }
    this.eventId = event.getEventId();
    this.fenDuiResult = event.getFenDuiResult();
    this.goTime = event.getGoTime();
    this.guests = event.getGuests();
    for (User user : event.getMembers()) {
      this.members.add(new UserVO(user));
    }
    this.name = event.getName();
    this.passengers = event.getPassengers();
    this.startTime = event.getStartTime();
    this.typeId = event.getTypeId();
  }

  public long getEventId() {
    return eventId;
  }

  public void setEventId(long eventId) {
    this.eventId = eventId;
  }

  @JsonIgnore
  public Date getStartTime() {
    return startTime;
  }

  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }

  @JsonIgnore
  public long getTypeId() {
    return typeId;
  }

  public void setTypeId(long typeId) {
    this.typeId = typeId;
  }

  @JsonIgnore
  public Set<UserVO> getDrivers() {
    return drivers;
  }

  public void setDrivers(Set<UserVO> drivers) {
    this.drivers = drivers;
  }

  @JsonIgnore
  public Map<Long, Set<Long>> getPassengers() {
    return passengers;
  }

  public void setPassengers(Map<Long, Set<Long>> passengers) {
    this.passengers = passengers;
  }

  public void setMembers(Set<UserVO> members) {
    this.members = members;
  }

  public int getOrdinal() {
    return Long.valueOf(this.eventId).intValue();
  }

  public String getTime() {
    return this.startTime.toString();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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

  @JsonIgnore
  public Map<Integer, List<Team>> getFenDuiResult() {
    return fenDuiResult;
  }

  public void setFenDuiResult(Map<Integer, List<Team>> fenDuiResult) {
    this.fenDuiResult = fenDuiResult;
  }

  public Set<String> getGuests() {
    return guests;
  }

  public void setGuests(Set<String> guests) {
    this.guests = guests;
  }

  public Set<UserVO> getMembers() {
    return members;
  }

  public Set<UserVO> getDrivingCarMembers() {
    return this.drivers;
  }

  public List<UserVO> getOrganizers() {
    Type type = ContextUtil.getContext().getType(typeId);
    if (type == null)
      return Collections.emptyList();
    return EntityUtil.userVOsOf(type.getOrganizers());
  }

  public String getType() {
    Type type = ContextUtil.getContext().getType(this.typeId);
    if (type == null) return "";
    return type.getName();
  }

  public int getTotalMembers() {
    Integer memberCount = ContextUtil.getContext().getEventMemberCount(
        this.eventId);
    if (memberCount == null) {
      return this.guests.size();
    } else {
      return memberCount + this.guests.size();
    }
  }

  /**
   * times user continously joined
   */
  public Map<String, Integer> getContinousTimes() {
    Type type = ContextUtil.getContext().getType(typeId);
    if (type == null) return Collections.emptyMap();
    Map<String, Integer> continousTimes = new HashMap<String, Integer>();
    Set<Event> events = type.getEvents();
    List<Event> eventList = new ArrayList<Event>(events);
    EntityUtil.sortEventByOrdinalDescNew(eventList);
    // only support latest event
    if (type.getLatestEvent() == null)
      return Collections.emptyMap();
    if (type.getLatestEvent().getEventId() != this.eventId)
      return Collections.emptyMap();
    Set<User> continousUsers = new HashSet<User>();
    int times = 1;
    for (Event event : eventList) {
      if (times == 1) {
        // first time, add all latestEvent joiners
        continousUsers.addAll(event.getMembers());
      } else {
        // each time, remove unjoining users
        Iterator<User> it = continousUsers.iterator();
        while (it.hasNext()) {
          User toCheckUser = it.next();
          if (!event.getMembers().contains(toCheckUser)) {
            it.remove();
          }
        }
      }
      // every round, update time record
      for (User continousUser : continousUsers) {
        continousTimes.put(continousUser.getEmail(), times);
      }
      // if no users to check anymore, return
      if (continousUsers.isEmpty()) {
        return continousTimes;
      }
      times++;
    }
    return continousTimes;
  }

  public Map<String, Set<String>> getCarPassengers() {
    Map<String, Set<String>> carPassengersMap = new HashMap<String, Set<String>>();
    for (Long driverId : this.passengers.keySet()) {
      if (this.passengers.get(driverId) == null)
        continue;
      Set<String> passengerEmails = new HashSet<String>();
      for (Long userId : this.passengers.get(driverId)) {
        User user = ContextUtil.getContext().getUser(userId); 
        if (user == null) continue;
        passengerEmails.add(user.getEmail());
      }
      User driver = ContextUtil.getContext().getUser(driverId);
      if (driver == null)
        continue;
      carPassengersMap.put(driver.getEmail(), passengerEmails);
    }
    return carPassengersMap;
  }

  public String getResult() {
    return "";
  }

}