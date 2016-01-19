package teamdivider.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import teamdivider.bean.eo.SequenceId;
import teamdivider.bean.eo.mapping.TypeSubscriber;
import teamdivider.dao.AbstractDAO;

@Repository
public class TypeSubscriberDAO extends AbstractDAO<TypeSubscriber> {

  @Autowired
  SequenceDAO sequenceDAO;
  
  @Override
  protected Class<TypeSubscriber> getClazz() {
    return TypeSubscriber.class;
  }
  
  public void create(TypeSubscriber mapping) {
    mapping.setMappingId(this.sequenceDAO.getNextSequenceId(SequenceId.SEQUENCE_TYPE_SUBSCRIBER));
    this.getBasicDAO().save(mapping);
  }

  public void removeSubscribersOfType(long typeId) {
    this.getBasicDAO().deleteByQuery(
        this.getBasicDAO().createQuery().filter("typeId", typeId));
  }
}