package teamdivider.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Reference;
import org.mongodb.morphia.annotations.Transient;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import teamdivider.util.ContextUtil;

@Embedded
public class ActivityEvent {
  protected int ordinal;
  protected String name;
  protected String time;
  protected String description;
  protected Date goTime;
  @Reference
  protected List<User> members = new ArrayList<User>();
  protected Map<Integer, List<Team>> fenDuiResults = new HashMap<Integer, List<Team>>();
  // temp users
  protected List<String> guests = new ArrayList<String>();
  @Reference
  protected Set<User> drivingCarMembers = new HashSet<User>();
  @JsonIgnore
  protected Map<ObjectId, Set<User>> carPassengersMap = new HashMap<ObjectId, Set<User>>();
  protected String type;
  // just used in join page
  @Transient
  protected int totalMembers;
  @Transient
  protected String result = "";

  protected ActivityEvent() {}

  public ActivityEvent(int ordinal, String name, String time, Date goTime) {
    this.ordinal = ordinal;
    this.name = name;
    this.time = time;
    this.goTime = goTime;
  }
  
  public int getOrdinal() {
    return ordinal;
  }
  
  public String getName() {
    return name;
  }
  
  public String getTime() {
    return time;
  }
  
  public List<User> getMembers() {
    return members;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
  
  public void updateFenDuiResult(List<Team> teams) {
    int numberTeams = teams.size();
    fenDuiResults.put(numberTeams, teams);
  }
  
  public List<Team> getFenDuiResult(int numberTeams) {
    List<Team> teams = fenDuiResults.get(numberTeams);
    if (teams == null) {
      return new ArrayList<Team>();
    }
    return teams;
  }
  
  public void addMember(User member) {
    if (this.hasUser(member.getUsername())) {
      return ;
    }
    this.getMembers().add(member);
  }
  
  public void removeMember(User user) {
    EntityUtil.removeUserFromList(this.getMembers(), user);
    this.drivingCarMembers.remove(user);
    this.carPassengersMap.remove(user.getId());
    for (ObjectId objectId : this.carPassengersMap.keySet()) {
      if (this.carPassengersMap.get(objectId) != null) {
        this.carPassengersMap.get(objectId).remove(user);
      }
    }
    for (int teamNumber : this.fenDuiResults.keySet()) {
      for (Team team : this.fenDuiResults.get(teamNumber)) {
        EntityUtil.removeUserFromList(team.getMembers(), user);
      }
    }
  }
  
  public boolean hasUser(String username) {
    for (User member : this.getMembers()) {
      if (member.getUsername().contains(username)) {
        return true;
      }
    }
    return false;
  }

  public Date getGoTime() {
    return goTime;
  }
  
  public void yesDrivingCar(User user) {
    this.drivingCarMembers.add(user);
  }
  
  public void noDrivingCar(User user) {
    this.drivingCarMembers.remove(user);
    this.carPassengersMap.remove(user.getId());
  }
  
  public Set<User> getDrivingCarMembers() {
    return this.drivingCarMembers;
  }

  public Map<Integer, List<Team>> getFenDuiResults() {
    return fenDuiResults;
  }

  public List<String> getGuests() {
    return guests;
  }

  public void setGuests(List<String> guests) {
    this.guests = guests;
  }
  
  public void addGuest(String guest) {
    if (!guest.isEmpty() && !guests.contains(guest)) {
      this.guests.add(guest);
    }
  }
  
  public void removeGuest(String guest) {
    this.guests.remove(guest);
  }
  
  public List<User> getOrganizers() {
    List<User> organizers = ContextUtil.ACTIVITY_TYPE_DAO
        .getActivityTypeByName(this.type).getOrganizers();
    return organizers;
  }

  public String getType() {
    if (StringUtils.isEmpty(this.type)) this.type = "soccer";
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
  
  public void simpleEvent() {
    this.totalMembers = this.members.size() + this.guests.size();
    this.members.clear();
    this.guests.clear();
    this.fenDuiResults.clear();
    this.drivingCarMembers.clear();
  }
  
  public int getTotalMembers() {
    return this.totalMembers;
  }

  /**
   * times user continously joined
   */
  public Map<String, Integer> getContinousTimes() {
    ActivityType type = ContextUtil.ACTIVITY_TYPE_DAO
        .getActivityTypeByName(this.type);
    Map<String, Integer> continousTimes = new HashMap<String, Integer>();
    List<ActivityEvent> events = type.getEvents();
    EntityUtil.sortEventByOrdinalDesc(events);
    if (events.get(0).getOrdinal() != this.ordinal) {
      return null; // only support latest event
    }
    Set<User> continousUsers = new HashSet<User>();
    int times = 1;
    for (ActivityEvent event : events) {
      if (times == 1) {
        // first time, add all latestEvent joiners
        continousUsers.addAll(event.getMembers());
      } else {
        // each time, remove unjoining users
        Iterator it = continousUsers.iterator();
        while (it.hasNext()) {
          User toCheckUser = (User) it.next();
          if (!event.getMembers().contains(toCheckUser)) {
            it.remove();
          }
        }
      }
      // every round, update time record
      for (User continousUser : continousUsers) {
        continousTimes.put(continousUser.getUsername(), times);
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
    for (ObjectId objectId : this.carPassengersMap.keySet()) {
      User driver = ContextUtil.USER_DAO.getUser(objectId);
      Set<String> passengers = new HashSet<String>();
      if (this.carPassengersMap.get(objectId) == null) {
        continue;
      }
      for (User passenger : this.carPassengersMap.get(objectId)) {
        passengers.add(passenger.getFullname());
      }
      carPassengersMap.put(driver.getUsername(), passengers);
    }
    return carPassengersMap;
  }
  
  public Set<User> getPassengers(User driver) {
    Set<User> passengers = this.carPassengersMap.get(driver.getId());
    if (passengers == null) {
      return new HashSet<User>();
    }
    return passengers;
  }
  
  @JsonIgnore
  public Set<String> getInCarUsers() {
    Set<String> inCarUsers = new HashSet<String>();
    for (ObjectId objectId : this.carPassengersMap.keySet()) {
      if (this.carPassengersMap.get(objectId) == null) {
        continue;
      } 
      for (User passenger : this.carPassengersMap.get(objectId)) {
        inCarUsers.add(passenger.getUsername());
      }
    }
    return inCarUsers;
  }
  
  public int byHisCar(User driver, User passenger) {
    if (this.carPassengersMap.get(driver.getId()) == null) {
      Set<User> passengers = new HashSet<User>();
      passengers.add(passenger);
      this.carPassengersMap.put(driver.getId(), passengers);
    } else {
      this.carPassengersMap.get(driver.getId()).add(passenger);
    }
    return this.carPassengersMap.get(driver.getId()).size();
  }
  
  public void notByHisCar(User driver, User passenger) {
    if (this.carPassengersMap.get(driver.getId()) != null) {
      this.carPassengersMap.get(driver.getId()).remove(passenger);
    }
  }
  
  public boolean inHisCar(User driver, User passenger) {
    if (this.carPassengersMap.get(driver.getId()) != null) {
      return this.carPassengersMap.get(driver.getId()).contains(passenger);
    }
    return false;
  }
  
  public List<String> findPassengerUserNames(User driver) {
    List<String> usernames = new ArrayList<String>();
    if (this.carPassengersMap.get(driver.getId()) != null) {
      for (User passenger : this.carPassengersMap.get(driver.getId())) {
        usernames.add(passenger.getUsername());
      }
    }
    return usernames;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result
        + ((description == null) ? 0 : description.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((time == null) ? 0 : time.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
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
    ActivityEvent other = (ActivityEvent) obj;
    if (description == null) {
      if (other.description != null)
        return false;
    } else if (!description.equals(other.description))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (time == null) {
      if (other.time != null)
        return false;
    } else if (!time.equals(other.time))
      return false;
    if (type == null) {
      if (other.type != null)
        return false;
    } else if (!type.equals(other.type))
      return false;
    return true;
  }
  
  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }
}
