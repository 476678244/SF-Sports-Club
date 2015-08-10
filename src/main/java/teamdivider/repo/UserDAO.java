package teamdivider.repo;

import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import teamdivider.entity.User;

@Repository
public class UserDAO {

  @Autowired
  private Datastore datastore;

  public User findByUsername(String username) {
    return datastore.find(User.class, "username", username).get();
  }

  public User saveUser(User user) {
    this.datastore.save(user);
    return user;
  }

  public List<User> allUsers() {
    return datastore.find(User.class).asList();
  }

  public User getUser(ObjectId id) {
    return datastore.get(User.class, id);
  }

  public void deleteUser(String username) {
    User user = this.findByUsername(username);
    if (user != null) {
      this.datastore.delete(user);
    }
  }

  public void deleteAll() {
    datastore.delete(datastore.createQuery(User.class));
  }
}
