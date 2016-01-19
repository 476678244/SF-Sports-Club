/*
 * $Id$
 */
package teamdivider.dao;

import org.springframework.stereotype.Repository;

import teamdivider.bean.eo.SequenceId;
import teamdivider.bean.eo.mapping.EventDriver;

@Repository
public class EventDriverDAO extends AbstractDAO<EventDriver> {

  @Override
  protected Class<EventDriver> getClazz() {
    return EventDriver.class;
  }

  public void create(EventDriver mapping) {
    mapping.setMappingId(
        this.sequenceDAO.getNextSequenceId(SequenceId.SEQUENCE_EVENT_DRIVER));
    this.getBasicDAO().save(mapping);
  }

  public void removeDriversOfEvent(long eventId) {
    this.getBasicDAO().deleteByQuery(
        this.getBasicDAO().createQuery().filter("eventId", eventId));
  }

  public void removeDriverFromEvent(long driverId, long eventId) {
    this.getBasicDAO().deleteByQuery(this.getBasicDAO().createQuery()
        .filter("eventId", eventId).filter("userId", driverId));
  }

}
