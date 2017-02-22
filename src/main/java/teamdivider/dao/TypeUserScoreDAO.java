/*
 * $Id$
 */
package teamdivider.dao;

import org.springframework.stereotype.Repository;

import teamdivider.bean.eo.SequenceId;
import teamdivider.bean.eo.UserScore;
import teamdivider.bean.eo.mapping.TypeUserScore;

@Repository
public class TypeUserScoreDAO extends AbstractDAO<TypeUserScore> {

  @Override
  protected Class<TypeUserScore> getClazz() {
    return TypeUserScore.class;
  }

  public void create(TypeUserScore mapping) {
    mapping.setMappingId(this.sequenceDAO
        .getNextSequenceId(SequenceId.SEQUENCE_TYPE_USER_SCORE));
    this.getBasicDAO().save(mapping);
  }

  public void removeUserScoresOfType(long typeId) {
    this.getBasicDAO().deleteByQuery(
        this.getBasicDAO().createQuery().filter("typeId", typeId));
  }

  public void upsert(TypeUserScore mapping) {
    TypeUserScore existMapping = this.getBasicDAO().find(this.getBasicDAO().createQuery().filter(
        "typeId", mapping.getTypeId()).filter("userId", mapping.getUserId())).get();
    if (existMapping == null) {
      this.create(mapping);
      return ;
    }
    existMapping.setScore(mapping.getScore());
    this.getBasicDAO().save(existMapping);
  }

  public UserScore getUserScore(long userId, long typeId) {
    TypeUserScore existMapping = this.getBasicDAO().find(
        this.getBasicDAO().createQuery().filter("typeId", typeId).filter("userId", userId))
        .get();
    return existMapping == null ? null : existMapping.getScore();
  }

}
