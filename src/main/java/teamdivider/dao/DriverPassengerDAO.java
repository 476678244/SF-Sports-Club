/*
 * $Id$
 */
package teamdivider.dao;

import org.springframework.stereotype.Repository;

import teamdivider.bean.eo.mapping.DriverPassenger;

@Repository
public class DriverPassengerDAO extends AbstractDAO<DriverPassenger> {

  @Override
  protected Class<DriverPassenger> getClazz() {
    return DriverPassenger.class;
  }

}
