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

  public void removeOrganizerFromTypes(long userId) {
    this.getBasicDAO().deleteByQuery(
        this.getBasicDAO().createQuery().filter("userId", userId));
  }

  public void removeOrganizerFromType(long userId, long typeId) {
    this.getBasicDAO().deleteByQuery(this.getBasicDAO().createQuery()
        .filter("typeId", typeId).filter("userId", userId));
  }
}
