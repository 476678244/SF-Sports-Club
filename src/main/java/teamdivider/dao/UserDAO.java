/*
 * $Id$
 */
package teamdivider.dao;

import org.springframework.stereotype.Repository;

import teamdivider.bean.eo.User;

@Repository("UserDAONEW")
public class UserDAO extends AbstractDAO<User> {

  @Override
  protected Class<User> getClazz() {
    return User.class;
  }

}
