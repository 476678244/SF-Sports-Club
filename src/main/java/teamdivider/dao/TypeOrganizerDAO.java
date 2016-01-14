package teamdivider.dao;

import org.springframework.stereotype.Repository;

import teamdivider.bean.eo.mapping.TypeOrganizer;
import teamdivider.dao.AbstractDAO;

@Repository
public class TypeOrganizerDAO extends AbstractDAO<TypeOrganizer> {

  @Override
  protected Class<TypeOrganizer> getClazz() {
    return TypeOrganizer.class;
  }

}
