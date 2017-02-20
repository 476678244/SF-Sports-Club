package teamdivider.bean.eo.mapping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * Created by wuzonghan on 17/2/20.
 */
@Data
@AllArgsConstructor
@Builder
@Entity
public class UserScore {
	@Id
	private ObjectId id;
	private long userId;
	private int attack;
	private int defend;
	private int skill;
	private int speed;
	private int stamina;
	private int strength;
}