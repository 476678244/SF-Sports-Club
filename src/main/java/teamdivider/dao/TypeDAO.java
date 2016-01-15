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
import teamdivider.bean.eo.Type;
import teamdivider.bean.eo.User;
import teamdivider.bean.eo.mapping.TypeEvent;
import teamdivider.bean.eo.mapping.TypeOrganizer;
import teamdivider.bean.eo.mapping.TypeSubscriber;
import teamdivider.bean.eo.mapping.TypeUserScore;

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

  public Type getTypeByName(String name) {
    Type type = this.getBasicDAO().findOne("name", name);
    return this.resolveTypeMappings(type);
  }
  
  public Type getTypeByTypeId(long typeId) {
    Type type = this.getBasicDAO().findOne("typeId", typeId);
    return this.resolveTypeMappings(type);
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

}
