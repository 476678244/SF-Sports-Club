/*
 * $Id$
 */
package teamdivider.dao;

import org.springframework.stereotype.Repository;

import teamdivider.bean.eo.mapping.EventMember;

@Repository
public class EventMemberDAO extends AbstractDAO<EventMember> {

  @Override
  protected Class<EventMember> getClazz() {
    return EventMember.class;
  }

}
