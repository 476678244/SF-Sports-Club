package teamdivider.dao;

import org.springframework.stereotype.Repository;

import teamdivider.bean.eo.mapping.TypeEvent;
import teamdivider.dao.AbstractDAO;

@Repository
public class TypeEventDAO extends AbstractDAO<TypeEvent> {

  @Override
  protected Class<TypeEvent> getClazz() {
    return TypeEvent.class;
  }

}
