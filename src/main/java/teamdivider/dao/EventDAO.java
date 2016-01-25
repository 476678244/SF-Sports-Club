/*
 * $Id$
 */
package teamdivider.dao;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import teamdivider.bean.eo.Event;
import teamdivider.bean.eo.SequenceId;
import teamdivider.bean.eo.User;
import teamdivider.bean.eo.mapping.DriverPassenger;
import teamdivider.bean.eo.mapping.EventDriver;
import teamdivider.bean.eo.mapping.EventFenDui;
import teamdivider.bean.eo.mapping.EventMember;
import teamdivider.entity.Team;
import teamdivider.util.ContextUtil;

@Repository
public class EventDAO extends AbstractDAO<Event> {

  @Autowired
  private DriverPassengerDAO driverPassengerDAO;

  @Autowired
  private EventDriverDAO eventDriverDAO;

  @Autowired
  private EventFenDuiDAO eventFenDuiDAO;

  @Autowired
  private EventMemberDAO eventMemberDAO;
  
  @Autowired
  private TypeEventDAO typeEventDAO;

  @Override
  protected Class<Event> getClazz() {
    return Event.class;
  }

  public Event getEventByEventId(long eventId) {
    return this.getEventByEventId(eventId, false);
  }
  
  public Event getEventByEventId(long eventId, boolean resolveEventMappings) {
    Event event = this.getBasicDAO().findOne("eventId", eventId);
    if (resolveEventMappings) {
      this.resolveEventMappings(event);
    }
    return event;
  }

  public EventDAO resolveEventMappings(Event event) {
    return this.resolveEventMembers(event).resolveEventDrivers(event)
        .resolveEventPassengers(event).resolveEventFenDui(event);
  }

  public EventDAO resolveEventMembers(Event event) {
    event.setMembers(this.getEventMembers(event.getEventId()));
    return this;
  }

  public EventDAO resolveEventDrivers(Event event) {
    event.setDrivers(this.getEventDrivers(event.getEventId()));
    return this;
  }

  public EventDAO resolveEventPassengers(Event event) {
    event.setPassengers(this.getDriverPassengers(event.getEventId()));
    return this;
  }

  public EventDAO resolveEventFenDui(Event event) {
    event.setFenDuiResult(this.getEventFenDui(event.getEventId()));
    return this;
  }

  private Set<User> getEventMembers(long eventId) {
    Query<EventMember> query = this.eventMemberDAO.getBasicDAO().createQuery();
    query.filter("eventId", eventId);
    List<EventMember> queryResults = this.eventMemberDAO.getBasicDAO()
        .find(query).asList();
    if (queryResults == null) {
      return Collections.emptySet();
    }
    Set<User> users = new HashSet<User>();
    for (EventMember mapping : queryResults) {
      ContextUtil.getContext().setUser(mapping.getUserId(), mapping.getUser());
      users.add(mapping.getUser());
    }
    return users;
  }

  private Set<User> getEventDrivers(long eventId) {
    Query<EventDriver> query = this.eventDriverDAO.getBasicDAO().createQuery();
    query.filter("eventId", eventId);
    List<EventDriver> queryResults = this.eventDriverDAO.getBasicDAO()
        .find(query).asList();
    if (queryResults == null) {
      return Collections.emptySet();
    }
    Set<User> users = new HashSet<User>();
    for (EventDriver mapping : queryResults) {
      ContextUtil.getContext().setUser(mapping.getUserId(), mapping.getUser());
      users.add(mapping.getUser());
    }
    return users;
  }

  private Map<Long, Set<Long>> getDriverPassengers(long eventId) {
    Query<DriverPassenger> query = this.driverPassengerDAO.getBasicDAO()
        .createQuery();
    query.filter("eventId", eventId);
    List<DriverPassenger> queryResults = this.driverPassengerDAO.getBasicDAO()
        .find(query).asList();
    if (queryResults == null)
      return Collections.emptyMap();
    Map<Long, Set<Long>> driver2Passengers = new HashMap<Long, Set<Long>>();
    for (DriverPassenger mapping : queryResults) {
      Set<Long> passengers = driver2Passengers.get(mapping.getDriverId());
      if (passengers == null) {
        passengers = new HashSet<Long>();
        passengers.add(mapping.getPassengerId());
        driver2Passengers.put(mapping.getDriverId(), passengers);
      } else {
        passengers.add(mapping.getPassengerId());
      }
    }
    return driver2Passengers;
  }

  private Map<Integer, List<Team>> getEventFenDui(long eventId) {
    Query<EventFenDui> query = this.eventFenDuiDAO.getBasicDAO().createQuery();
    query.filter("eventId", eventId);
    List<EventFenDui> queryResults = this.eventFenDuiDAO.getBasicDAO()
        .find(query).asList();
    if (queryResults == null)
      return Collections.emptyMap();
    Map<Integer, List<Team>> fenDui = new HashMap<Integer, List<Team>>();
    for (EventFenDui mapping : queryResults) {
      fenDui.put(mapping.getTeamNumber(), mapping.getTeams());
    }
    return fenDui;
  }
  
  public void removeUserFromEvents(long userId) {
    this.eventDriverDAO.getBasicDAO().deleteByQuery(this.eventDriverDAO
        .getBasicDAO().createQuery().filter("userId", userId));
    this.driverPassengerDAO.getBasicDAO().deleteByQuery(this.driverPassengerDAO
        .getBasicDAO().createQuery().filter("passengerId", userId));
    this.eventMemberDAO.getBasicDAO().deleteByQuery(this.eventMemberDAO
        .getBasicDAO().createQuery().filter("userId", userId));
    // don`t remove user from fendui result
  }
  
  public void create(Event event) {
    event.setEventId(this.sequenceDAO.getNextSequenceId(SequenceId.SEQUENCE_EVENT));
    this.getBasicDAO().save(event);
  }
  
  public void addMember(long eventId, long userId, User user) {
    EventMember mapping = new EventMember();
    mapping.setEventId(eventId);
    mapping.setUserId(userId);
    mapping.setUser(user);
    this.eventMemberDAO.create(mapping);
  }

  public void removeMember(long eventId, long userId) {
    this.eventMemberDAO.removeMemebrFromEvent(userId, eventId);
  }

  public void removeDriver(long eventId, long userId) {
    this.eventDriverDAO.removeDriverFromEvent(userId, eventId);
    this.driverPassengerDAO.removeDriversOfEvent(userId, eventId);
  }

  public void removePassenger(long eventId, long userId) {
    this.driverPassengerDAO.removePassengerFromEvent(userId, eventId);
  }
  
  public void removeUserInEvent(long eventId, long userId) {
    this.removeDriver(eventId, userId);
    this.removePassenger(eventId, userId);
    this.removeMember(eventId, userId);
  }
  
  public void addDriver(long eventId, User user) {
    EventDriver mapping = new EventDriver();
    mapping.setEventId(eventId);
    mapping.setUserId(user.getUserId());
    mapping.setUser(user);
    this.eventDriverDAO.create(mapping);
  }
  
  public void addPassenger(long eventId, long driverId, long passengerId) {
    DriverPassenger mapping = new DriverPassenger();
    mapping.setDriverId(driverId);
    mapping.setEventId(eventId);
    mapping.setPassengerId(passengerId);
    this.driverPassengerDAO.create(mapping);
  }
  
  public void save(Event event) {
    this.getBasicDAO().save(event);
  }
  
  public void deleteEvent(long eventId) {
    this.typeEventDAO.removeEventFromTypes(eventId);
    this.eventDriverDAO.removeDriversOfEvent(eventId);
    this.driverPassengerDAO.removePassengersOfEvent(eventId);
    this.eventFenDuiDAO.removeFenDuisOfEvent(eventId);
    this.eventMemberDAO.removeMembersOfEvent(eventId);
    this.getBasicDAO().deleteByQuery(
        this.getBasicDAO().createQuery().filter("eventId", eventId));
  }

  public void resolveMemberCountToContext(long eventId) {
    this.eventMemberDAO.resolveMemberCountToContext(eventId);
  }
}
