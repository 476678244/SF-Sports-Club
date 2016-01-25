package teamdivider.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import teamdivider.bean.eo.Event;
import teamdivider.bean.eo.Type;
import teamdivider.bean.vo.EventVO;
import teamdivider.bean.vo.TypeVO;
import teamdivider.bean.vo.UserVO;

public class EntityUtil {
  public static void removeUserFromList(List<User> users, User user) {
    Iterator<User> it = users.iterator();
    while (it.hasNext()) {
      User member = (User) it.next();
      if (member.getUsername().equals(user.getUsername())) {
        it.remove();
        return;
      }
    }
  }
  
  public static Team weakestTeam(List<Team> teams) {
    Team weakestTeam = teams.get(0);
    for (Team team : teams) {
      if (memberSize(team) > memberSize(weakestTeam)) {
        continue;
      } else if (team.getMembers().size() == weakestTeam.getMembers().size()) {
        if (team.getScore() < weakestTeam.getScore()) {
          weakestTeam = team;
        }
      } else {
        weakestTeam = team;
      }
    }
    return weakestTeam;
  }

  private static int memberSize(Team team) {
    return team.getMembers().size() + team.getGuests().size();
  }
  
  // for example: 
  //            5 means 5 score striker
  //            106 means 6 score defender
  //            207 means 7 score middle fielder
  //            305 means 5 score goal keeper
  // also suppose back field player is more important then front field player
  public static int simpleScore(int richScore) {
    return richScore%100;
  }
  
  public static void sortJoinersByScoreAsc(List<User> joiners) {
    Collections.sort(joiners, new Comparator<User>() {
      public int compare(User joiner1, User joiner2) {
        return new Integer(joiner1.getScore()).compareTo(new Integer(joiner2
            .getScore()));
      }
    });
  }

  public static void sortEventByOrdinalDesc(List<ActivityEvent> events) {
    Collections.sort(events, new Comparator<ActivityEvent>() {
      public int compare(ActivityEvent event1, ActivityEvent event2) {
        return new Integer(event2.getOrdinal()).compareTo(new Integer(event1
            .getOrdinal()));
      }
    });
  }
  
  public static void sortEventByOrdinalAsc(List<ActivityEvent> events) {
    Collections.sort(events, new Comparator<ActivityEvent>() {
      public int compare(ActivityEvent event1, ActivityEvent event2) {
        return new Integer(event1.getOrdinal()).compareTo(new Integer(event2
            .getOrdinal()));
      }
    });
  }
  
  public static void sortEventByOrdinalDescNew(List<Event> events) {
    Collections.sort(events, new Comparator<Event>() {
      public int compare(Event event1, Event event2) {
        return new Long(event2.getEventId())
            .compareTo(new Long(event1.getEventId()));
      }
    });
  }
  
  public static void sortEventVOByOrdinalDescNew(List<EventVO> events) {
    Collections.sort(events, new Comparator<EventVO>() {
      public int compare(EventVO event1, EventVO event2) {
        return new Long(event2.getEventId())
            .compareTo(new Long(event1.getEventId()));
      }
    });
  }

  public static void sortTypesByPriorityDesc(List<ActivityType> types) {
    Collections.sort(types, new Comparator<ActivityType>() {
      public int compare(ActivityType type1, ActivityType type2) {
        return typePriorityMap.get(type2.getName()).compareTo(
            typePriorityMap.get(type1.getName()));
      }
    });
  }

  private static Map<String, Integer> typePriorityMap = new HashMap<String, Integer>();

  static {
    typePriorityMap.put("soccer", 10);
    typePriorityMap.put("badminton", 9);
    typePriorityMap.put("boardgame", 8);
    typePriorityMap.put("bicycle", 7);
    typePriorityMap.put("dance", 5);
    typePriorityMap.put("yoga", 4);
    typePriorityMap.put("tennis", 2);
    typePriorityMap.put("tabletennis", 3);
    typePriorityMap.put("basketball", 1);
  }

  public static Event eventOf(EventVO vo) {
    if (vo == null) return null;
    Event event = Event.builder().eventId(vo.getEventId()).name(vo.getName())
        .startTime(vo.getStartTime()).goTime(vo.getGoTime())
        .description(vo.getDescription()).typeId(vo.getTypeId())
        .guests(vo.getGuests()).build();
    return event;
  }

  public static teamdivider.bean.eo.User userOf(UserVO vo) {
    if (vo == null) return null;
    teamdivider.bean.eo.User user = new teamdivider.bean.eo.User();
    user.setUserId(vo.getUserId());
    user.setEmail(vo.getEmail());
    user.setFullName(vo.getFullName());
    user.setAvatar(vo.getAvatar());
    return user;
  }

  public static Type typeOf(TypeVO vo) {
    if (vo == null) return null;
    Type type = Type.builder().typeId(vo.getTypeId()).name(vo.getName())
        .latestEvent(eventOf(vo.getLatestEvent())).build();
    return type;
  }
  
  public static List<UserVO> userVOsOf(Collection<teamdivider.bean.eo.User> eos) {
    List<UserVO> vos = new ArrayList<UserVO>(eos.size());
    for (teamdivider.bean.eo.User eo : eos) {
      vos.add(new UserVO(eo));
    }
    return vos;
  }
  
  public static void sortTypeVOsByPriorityDesc(List<TypeVO> types) {
    Collections.sort(types, new Comparator<TypeVO>() {
      public int compare(TypeVO type1, TypeVO type2) {
        if (typePriorityMap.get(type2.getName()) == null ) return 0;
        if (typePriorityMap.get(type1.getName()) == null ) return 0;
        return typePriorityMap.get(type2.getName()).compareTo(
            typePriorityMap.get(type1.getName()));
      }
    });
  }

}
