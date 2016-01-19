package teamdivider.dao;

import org.springframework.stereotype.Repository;

import teamdivider.bean.eo.SequenceId;
import teamdivider.bean.eo.mapping.TypeEvent;
import teamdivider.dao.AbstractDAO;

@Repository
public class TypeEventDAO extends AbstractDAO<TypeEvent> {

  @Override
  protected Class<TypeEvent> getClazz() {
    return TypeEvent.class;
  }

  public void create(TypeEvent mapping) {
    mapping.setMappingId(
        this.sequenceDAO.getNextSequenceId(SequenceId.SEQUENCE_TYPE_EVENT));
    this.getBasicDAO().save(mapping);
  }

  public void removeEventFromTypes(long eventId) {
    this.getBasicDAO().deleteByQuery(
        this.getBasicDAO().createQuery().filter("eventId", eventId));
  }

  public void removeEventFromType(long eventId, long typeId) {
    this.getBasicDAO().deleteByQuery(this.getBasicDAO().createQuery()
        .filter("eventId", eventId).filter("typeId", typeId));
  }

}
