package teamdivider.bean.eo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by wuzonghan on 17/2/21.
 */
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class UserScore {
	private int attack;
	private int defend;
	private int skill;
	private int speed;
	private int stamina;
	private int strength;
}