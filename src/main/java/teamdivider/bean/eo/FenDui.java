/*
 * $Id$
 */
package teamdivider.bean.eo;

import java.util.HashSet;
import java.util.Set;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import teamdivider.entity.Team;

@Entity
public class FenDui {

  @Id
  private ObjectId id;
  
  private long fenDuiId;
  
  private Set<Team> teams = new HashSet<Team>();

  public ObjectId getId() {
    return id;
  }

  public void setId(ObjectId id) {
    this.id = id;
  }

  public long getFenDuiId() {
    return fenDuiId;
  }

  public void setFenDuiId(long fenDuiId) {
    this.fenDuiId = fenDuiId;
  }

  public Set<Team> getTeams() {
    return teams;
  }

  public void setTeams(Set<Team> teams) {
    this.teams = teams;
  }
  
}
