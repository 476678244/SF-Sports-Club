/*
 * $Id$
 */
package teamdivider.dao;

import org.springframework.stereotype.Repository;

import teamdivider.bean.eo.SequenceId;
import teamdivider.bean.eo.mapping.DriverPassenger;

@Repository
public class DriverPassengerDAO extends AbstractDAO<DriverPassenger> {

  @Override
  protected Class<DriverPassenger> getClazz() {
    return DriverPassenger.class;
  }

  public void create(DriverPassenger mapping) {
    mapping.setMappingId(this.sequenceDAO
        .getNextSequenceId(SequenceId.SEQUENCE_DRIVER_PASSENGER));
    this.getBasicDAO().save(mapping);
  }

  public void removePassengerFromEvent(long passengerId, long eventId) {
    this.getBasicDAO().deleteByQuery(this.getBasicDAO().createQuery()
        .filter("passengerId", passengerId).filter("eventId", eventId));
  }

  public void removePassengersOfEvent(long eventId) {
    this.getBasicDAO().deleteByQuery(
        this.getBasicDAO().createQuery().filter("eventId", eventId));
  }

  public void removeDriversOfEvent(long driverId, long eventId) {
    this.getBasicDAO().deleteByQuery(this.getBasicDAO().createQuery()
        .filter("driverId", driverId).filter("eventId", eventId));
  }
  
  public boolean isUserInCar(long eventId, long userId) {
    long count = this.getBasicDAO().createQuery().filter("eventId", eventId)
        .filter("passengerId", userId).countAll();
    return count > 0;
  }

}
