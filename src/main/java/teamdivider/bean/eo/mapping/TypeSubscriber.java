/*
 * $Id$
 */
package teamdivider.bean.eo.mapping;

import org.mongodb.morphia.annotations.Entity;

@Entity
public class TypeSubscriber extends ToUserMapping {

  private long typeId;

  public long getTypeId() {
    return typeId;
  }

  public void setTypeId(long typeId) {
    this.typeId = typeId;
  }

}
