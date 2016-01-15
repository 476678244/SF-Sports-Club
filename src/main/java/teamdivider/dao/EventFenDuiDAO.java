/*
 * $Id$
 */
package teamdivider.dao;

import org.springframework.stereotype.Repository;

import teamdivider.bean.eo.mapping.EventFenDui;

@Repository
public class EventFenDuiDAO extends AbstractDAO<EventFenDui> {

  @Override
  protected Class<EventFenDui> getClazz() {
    return EventFenDui.class;
  }

}
