/*
 * $Id$
 */
package teamdivider.dao;

import org.springframework.stereotype.Repository;

import teamdivider.bean.eo.mapping.TypeUserScore;

@Repository
public class TypeUserScoreDAO extends AbstractDAO<TypeUserScore> {

  @Override
  protected Class<TypeUserScore> getClazz() {
    return TypeUserScore.class;
  }

}
