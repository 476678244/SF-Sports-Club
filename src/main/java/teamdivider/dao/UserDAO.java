/*
 * $Id$
 */
package teamdivider.dao;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import teamdivider.bean.eo.SequenceId;
import teamdivider.bean.eo.Type;
import teamdivider.bean.eo.User;
import teamdivider.bean.eo.mapping.TypeSubscriber;

@Repository("UserDAOV2")
public class UserDAO extends AbstractDAO<User> {

  @Autowired
  SequenceDAO sequenceDAO;

  @Autowired
  TypeSubscriberDAO typeSubscriberDAO;

  @Autowired
  TypeDAO typeDAO;

  @Override
  protected Class<User> getClazz() {
    return User.class;
  }

  public User findByEmail(String email) {
    User user = this.getBasicDAO().findOne("email", email);
    if (user == null) return null;
    user.setSubscribedTypes(this.getSubscribedTypes(user.getUserId()));
    return user;
  }
  

  public User findByUserId(long userId) {
    User user = this.getBasicDAO().findOne("userId", userId);
    user.setSubscribedTypes(this.getSubscribedTypes(user.getUserId()));
    return user;
  }

  private Set<Type> getSubscribedTypes(long userId) {
    Query<TypeSubscriber> querySubscribers = this.typeSubscriberDAO
        .getBasicDAO().createQuery().filter("userId", userId);
    List<TypeSubscriber> typeSubscribers = querySubscribers.asList();
    if (typeSubscribers == null) {
      return Collections.emptySet();
    }
    Set<Type> types = new HashSet<Type>(typeSubscribers.size());
    for (TypeSubscriber mapping : typeSubscribers) {
      types.add(this.typeDAO.getTypeByTypeId(mapping.getTypeId(), false));
    }
    return types;
  }

  public List<User> allUsers() {
    return this.getBasicDAO().find().asList();
  }

  public User saveUser(User user) {
    this.getBasicDAO().save(user);
    return user;
  }

  public void create(User user) {
    user.setUserId(
        this.sequenceDAO.getNextSequenceId(SequenceId.SEQUENCE_USER));
    this.getBasicDAO().save(user);
  }

  public void deleteUser(long userId) {
    this.typeDAO.removeTypeUserMapping(userId);
    this.getBasicDAO().deleteByQuery(
        this.getBasicDAO().createQuery().filter("userId", userId));
  }

}
