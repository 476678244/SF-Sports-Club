/*
 * $Id$
 */
package teamdivider.dao;

import org.springframework.stereotype.Repository;

import teamdivider.bean.eo.Event;

@Repository
public class EventDAO extends AbstractDAO<Event> {

  @Override
  protected Class<Event> getClazz() {
    return Event.class;
  }

}
