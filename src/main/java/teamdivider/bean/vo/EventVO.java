/*
 * $Id$
 */
package teamdivider.bean.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import teamdivider.bean.eo.Event;
import teamdivider.bean.eo.Type;
import teamdivider.bean.eo.User;
import teamdivider.entity.ActivityEvent;
import teamdivider.entity.EntityUtil;
import teamdivider.entity.Team;
import teamdivider.util.ContextUtil;

public class EventVO extends ActivityEvent {

  private long eventId;

  private Date startTime;

  private long typeId;

  private List<UserVO> members = new ArrayList<UserVO>();

  private Set<UserVO> drivers = new HashSet<UserVO>();

  private Map<Long, Set<Long>> passengers = new HashMap<Long, Set<Long>>();

  public EventVO(Event event) {
    this.description = event.getDescription();
    for (User user : event.getDrivers()) {
      this.drivers.add(new UserVO(user));
    }
    this.eventId = event.getEventId();
    this.fenDuiResults = event.getFenDuiResult();
    this.goTime = event.getGoTime();
    this.guests = new ArrayList<String>(event.getGuests());
    for (User user : event.getMembers()) {
      this.members.add(new UserVO(user));
    }
    this.name = event.getName();
    this.passengers = event.getPassengers();
    this.startTime = event.getStartTime();
    this.typeId = event.getTypeId();
  }

  @JsonIgnore
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

  public void setMembers(List<UserVO> members) {
    this.members = members;
  }

  public int getOrdinal() {
    return Long.valueOf(this.eventId).intValue();
  }

  public String getTime() {
    return this.startTime.toString();
  }

  public List<teamdivider.entity.User> getMembers() {
    return EntityUtil.userVOsToUsers(members);
  }

  public List<Team> getFenDuiResult(int numberTeams) {
    List<Team> teams = fenDuiResults.get(numberTeams);
    if (teams == null) {
      return new ArrayList<Team>();
    }
    return teams;
  }

  public Set<teamdivider.entity.User> getDrivingCarMembers() {
    return EntityUtil.userVOsToUsers(drivers);
  }

  public List<teamdivider.entity.User> getOrganizers() {
    Type type = ContextUtil.getContext().getType(this.typeId);
    List<UserVO> userVOs = new ArrayList<UserVO>();
    for (User user : type.getOrganizers()) {
      userVOs.add(new UserVO(user));
    }
    return EntityUtil.userVOsToUsers(userVOs);
  }

  public String getType() {
    Type type = ContextUtil.getContext().getType(this.typeId);
    return type.getName();
  }

  public int getTotalMembers() {
    return this.members.size() + this.guests.size();
  }

  /**
   * times user continously joined
   */
  public Map<String, Integer> getContinousTimes() {
    Type type = ContextUtil.getContext().getType(typeId);
    Map<String, Integer> continousTimes = new HashMap<String, Integer>();
    Set<Event> events = type.getEvents();
    List<Event> eventList = new ArrayList<Event>(events);
    EntityUtil.sortEventByOrdinalDescNew(eventList);
    if (eventList.get(0).getEventId() != this.eventId) {
      return null; // only support latest event
    }
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
        passengerEmails
            .add(ContextUtil.getContext().getUser(userId).getEmail());
      }
      carPassengersMap.put(
          ContextUtil.getContext().getUser(driverId).getEmail(),
          passengerEmails);
    }
    return carPassengersMap;
  }

  public String getResult() {
    return result;
  }

}
