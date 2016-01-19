/*
 * $Id$
 */
package teamdivider.dao;

import org.springframework.stereotype.Repository;

import teamdivider.bean.eo.SequenceId;
import teamdivider.bean.eo.mapping.EventFenDui;

@Repository
public class EventFenDuiDAO extends AbstractDAO<EventFenDui> {

  @Override
  protected Class<EventFenDui> getClazz() {
    return EventFenDui.class;
  }
  
  public void create(EventFenDui mapping) {
    mapping.setMappingId(this.sequenceDAO.getNextSequenceId(SequenceId.SEQUENCE_EVENT_FENDUI));
    this.getBasicDAO().save(mapping);
  }
  
  public void removeFenDuisOfEvent(long eventId) {
    this.getBasicDAO().deleteByQuery(
        this.getBasicDAO().createQuery().filter("eventId", eventId));
  }

}
