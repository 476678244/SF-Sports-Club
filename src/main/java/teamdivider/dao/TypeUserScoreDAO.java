/*
 * $Id$
 */
package teamdivider.dao;

import org.springframework.stereotype.Repository;

import teamdivider.bean.eo.SequenceId;
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

}
