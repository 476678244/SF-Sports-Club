package teamdivider.entity;

import java.util.ArrayList;
import java.util.List;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Reference;

import teamdivider.util.PropertyUtil;

@Embedded
public class Team {
  private String name;
  private int score;
  @Reference
  private List<User> members = new ArrayList<User>();
  private List<String> guests = new ArrayList<String>();
  
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  
  public int getScore() {
    return score;
  }
  public void setScore(int score) {
    this.score = score;
  }
  
  public List<User> getMembers() {
    return members;
  }
  
  @Override
  public String toString() {
    return "Team("+name+")";
  }

  public List<String> getGuests() {
    return guests;
  }
  
  public void setGuests(List<String> guests) {
    this.guests = guests;
  }
  
  public void addGuest(String guest) {
    this.guests.add(guest);
    this.score += PropertyUtil.DEFAULT_USER_SCORE;
  } 
  
}
