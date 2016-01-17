/*
 * $Id$
 */
package teamdivider.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import teamdivider.bean.eo.SequenceId;
import teamdivider.bean.eo.User;

@Repository("UserDAOV2")
public class UserDAO extends AbstractDAO<User> {

  @Autowired
  SequenceDAO sequenceDAO;
  
  @Override
  protected Class<User> getClazz() {
    return User.class;
  }

  public User findByEmail(String email) {
    return this.getBasicDAO().findOne("email", email);
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
    this.getBasicDAO().deleteByQuery(
        this.getBasicDAO().createQuery().filter("userId", userId));
  }

}
