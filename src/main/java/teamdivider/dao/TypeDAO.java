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
import teamdivider.bean.eo.Type;
import teamdivider.bean.eo.User;
import teamdivider.bean.eo.mapping.TypeEvent;
import teamdivider.bean.eo.mapping.TypeOrganizer;
import teamdivider.bean.eo.mapping.TypeSubscriber;
import teamdivider.bean.eo.mapping.TypeUserScore;
import teamdivider.util.ContextUtil;

@Repository
public class TypeDAO extends AbstractDAO<Type> {

  @Autowired
  TypeEventDAO typeEventDAO;

  @Autowired
  TypeOrganizerDAO typeOrganizerDAO;

  @Autowired
  TypeSubscriberDAO typeSubscriberDAO;

  @Autowired
  TypeUserScoreDAO typeUserScoreDAO;
  
  @Autowired
  SequenceDAO sequenceDAO;
  
  @Autowired
  EventDAO eventDAO;

  public Type getTypeByName(String name) {
    return this.getTypeByName(name, false);
  }
  
  public Type getTypeByName(String name, boolean resolveTypeMappings) {
    Type type = this.getBasicDAO().findOne("name", name);
    if (resolveTypeMappings) {
      ContextUtil.getContext().setType(type.getTypeId(), type);
      this.resolveTypeMappings(type);
    }
    return type;
  }
  
  public Type getTypeByTypeId(long typeId) {
    return this.getTypeByTypeId(typeId, false);
  }
  
  public Type getTypeByTypeId(long typeId, boolean resolveTypeMappings) {
    Type type = this.getBasicDAO().findOne("typeId", typeId);
    if (resolveTypeMappings) {
      ContextUtil.getContext().setType(type.getTypeId(), type);
      this.resolveTypeMappings(type);
    }
    return type;
  }
  
  private Type resolveTypeMappings(Type type) {
    type.setEvents(this.getTypeEvents(type.getTypeId()));
    type.setOrganizers(this.getTypeOrganizers(type.getTypeId()));
    type.setSubscribers(this.getTypeSubscribers(type.getTypeId()));
    type.setScores(this.getUserScore(type.getTypeId()));
    return type;
  }

  private Set<Event> getTypeEvents(long typeId) {
    Query<TypeEvent> queryEvents = this.typeEventDAO.getBasicDAO()
        .createQuery();
    queryEvents.filter("typeId", typeId);
    List<TypeEvent> typeEvents = this.typeEventDAO.getBasicDAO()
        .find(queryEvents).asList();
    if (typeEvents == null) {
      return Collections.emptySet();
    }
    Set<Event> events = new HashSet<Event>(typeEvents.size());
    for (TypeEvent mapping : typeEvents) {
      events.add(mapping.getEvent());
    }
    return events;
  }
  
  private Set<User> getTypeOrganizers(long typeId) {
    Query<TypeOrganizer> queryOrganizers = this.typeOrganizerDAO.getBasicDAO()
        .createQuery();
    queryOrganizers.filter("typeId", typeId);
    List<TypeOrganizer> typeOrganizers = this.typeOrganizerDAO.getBasicDAO()
        .find(queryOrganizers).asList();
    if (typeOrganizers == null) {
      return Collections.emptySet();
    }
    Set<User> users = new HashSet<User>(typeOrganizers.size());
    for (TypeOrganizer mapping : typeOrganizers) {
      users.add(mapping.getUser());
    }
    return users;
  }
  
  private Set<User> getTypeSubscribers(long typeId) {
    Query<TypeSubscriber> querySubscribers = this.typeSubscriberDAO.getBasicDAO()
        .createQuery();
    querySubscribers.filter("typeId", typeId);
    List<TypeSubscriber> typeSubscribers = this.typeSubscriberDAO.getBasicDAO()
        .find(querySubscribers).asList();
    if (typeSubscribers == null) {
      return Collections.emptySet();
    }
    Set<User> users = new HashSet<User>(typeSubscribers.size());
    for (TypeSubscriber mapping : typeSubscribers) {
      users.add(mapping.getUser());
    }
    return users;
  }
  
  private Map<Long, Integer> getUserScore(long typeId) {
    Query<TypeUserScore> query = this.typeUserScoreDAO.getBasicDAO()
        .createQuery();
    query.filter("typeId", typeId);
    List<TypeUserScore> queryResults = this.typeUserScoreDAO.getBasicDAO().find(query).asList();
    if (queryResults == null) {
      return Collections.emptyMap();
    }
    Map<Long, Integer> userScore = new HashMap<Long, Integer>();
    for (TypeUserScore mapping : queryResults) {
      userScore.put(mapping.getUserId(), mapping.getScore());
    }
    return userScore;
  } 

  @Override
  protected Class<Type> getClazz() {
    return Type.class;
  }

  public List<Type> getAllActivityTypes() {
    return this.getBasicDAO().find().asList();
  }
  
  public List<Type> getAllActivityTypes(boolean resolveSubscribers) {
    List<Type> types = this.getBasicDAO().find().asList();
    for (Type type : types) {
      if (resolveSubscribers) {
        this.resolveTypeMappings(type);
      }
    }
    return types;
  }

  public void saveActivityType(Type type) {
    this.getBasicDAO().save(type);
  }
  
  public void create(Type type) {
    type.setTypeId(this.sequenceDAO.getNextSequenceId(SequenceId.SEQUENCE_TYPE));
    this.getBasicDAO().save(type);
  }
  
  public void userSubscribe(long userId, long typeId, User user) {
    TypeSubscriber mapping = new TypeSubscriber();
    mapping.setTypeId(typeId);
    mapping.setUserId(userId);
    mapping.setUser(user);
    this.typeSubscriberDAO.create(mapping);
  }

  public void userUnSubscribe(long userId, long typeId) {
    this.typeSubscriberDAO.getBasicDAO()
        .deleteByQuery(this.typeSubscriberDAO.getBasicDAO().createQuery()
            .filter("userId", userId).filter("typeId", typeId));
  }
  
  public void removeTypeUserMapping(long userId) {
    this.typeOrganizerDAO.removeOrganizerFromTypes(userId);
    this.typeSubscriberDAO.getBasicDAO().deleteByQuery(this.typeSubscriberDAO
        .getBasicDAO().createQuery().filter("userId", userId));
    this.eventDAO.removeUserFromEvents(userId);
  }

  public void addEvent(Event event) {
    TypeEvent mapping = new TypeEvent();
    mapping.setEvent(event);
    mapping.setEventId(event.getEventId());
    mapping.setTypeId(event.getTypeId());
    this.typeEventDAO.create(mapping);
  }

  public void addOrganizer(long typeId, long userId, User user) {
    TypeOrganizer mapping = new TypeOrganizer();
    mapping.setTypeId(typeId);
    mapping.setUser(user);
    mapping.setUserId(userId);
    this.typeOrganizerDAO.create(mapping);
  }

  public void removeOrganizer(long typeId, long userId) {
    this.typeOrganizerDAO.removeOrganizerFromType(userId, typeId);
  }
  
  public void removeEvent(long eventId) {
    this.typeEventDAO.removeEventFromTypes(eventId);
    this.eventDAO.deleteEvent(eventId);
  }

  public void deleteType(Type type) {
    this.typeOrganizerDAO.removeOrganizersOfType(type.getTypeId());
    this.typeSubscriberDAO.removeSubscribersOfType(type.getTypeId());
    this.typeUserScoreDAO.removeUserScoresOfType(type.getTypeId());
    for (Event event : type.getEvents()) {
      this.eventDAO.deleteEvent(event.getEventId());
    }
    this.getBasicDAO().delete(type);
  }

}
