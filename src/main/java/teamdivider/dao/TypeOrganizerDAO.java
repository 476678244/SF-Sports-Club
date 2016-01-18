package teamdivider.dao;

import org.springframework.stereotype.Repository;

import teamdivider.bean.eo.SequenceId;
import teamdivider.bean.eo.mapping.TypeOrganizer;
import teamdivider.dao.AbstractDAO;

@Repository
public class TypeOrganizerDAO extends AbstractDAO<TypeOrganizer> {

  @Override
  protected Class<TypeOrganizer> getClazz() {
    return TypeOrganizer.class;
  }

  public void create(TypeOrganizer mapping) {
    mapping.setMappingId(
        this.sequenceDAO.getNextSequenceId(SequenceId.SEQUENCE_TYPE_ORGANIZER));
    this.getBasicDAO().save(mapping);
  }
}
