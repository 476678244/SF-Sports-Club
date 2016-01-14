/*
 * $Id$
 */
package teamdivider.bean.eo.mapping;

import org.mongodb.morphia.annotations.Entity;

@Entity
public class EventDriver extends ToUserMapping {

  private long eventId;

  public long getEventId() {
    return eventId;
  }

  public void setEventId(long eventId) {
    this.eventId = eventId;
  }
}
