/*
 * $Id$
 */
package teamdivider.bean.eo.mapping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import teamdivider.bean.eo.UserScore;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TypeUserScore {

  @Id
  private ObjectId id;
  
  private long mappingId;
  
  private long userId;
  
  private long typeId;
  
  private UserScore score;

}
