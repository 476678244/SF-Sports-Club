/*
 * $Id$
 */
package teamdivider.dao;

import org.springframework.stereotype.Repository;

import teamdivider.bean.eo.SequenceId;
import teamdivider.bean.eo.mapping.EventMember;
import teamdivider.util.ContextUtil;

@Repository
public class EventMemberDAO extends AbstractDAO<EventMember> {

  @Override
  protected Class<EventMember> getClazz() {
    return EventMember.class;
  }

  public void create(EventMember mapping) {
    mapping.setMappingId(
        this.sequenceDAO.getNextSequenceId(SequenceId.SEQUENCE_EVENT_MEMBER));
    this.getBasicDAO().save(mapping);
  }

  public void removeMembersOfEvent(long eventId) {
    this.getBasicDAO().deleteByQuery(
        this.getBasicDAO().createQuery().filter("eventId", eventId));
  }

  public void removeMemebrFromEvent(long memberId, long eventId) {
    this.getBasicDAO().deleteByQuery(this.getBasicDAO().createQuery()
        .filter("eventId", eventId).filter("userId", memberId));
  }

  public void resolveMemberCountToContext(long eventId) {
    Long count = this.getBasicDAO().createQuery().filter("eventId", eventId)
        .countAll();
    ContextUtil.getContext().putEventMemberCount(eventId,
        Integer.valueOf(count.toString()));
  }
}
