/*
 * $Id$
 */
package teamdivider.dao;

import org.springframework.stereotype.Repository;

import teamdivider.bean.eo.mapping.EventDriver;

@Repository
public class EventDriverDAO extends AbstractDAO<EventDriver> {

  @Override
  protected Class<EventDriver> getClazz() {
    return EventDriver.class;
  }

}
