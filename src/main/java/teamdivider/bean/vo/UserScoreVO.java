package teamdivider.bean.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import teamdivider.bean.eo.mapping.UserScore;

/**
 * Created by wuzonghan on 17/2/20.
 */
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class UserScoreVO {

	private long userId;
	private int attack;
	private int defend;
	private int skill;
	private int speed;
	private int stamina;
	private int strength;

	public UserScoreVO(UserScore userScore) {
		this.userId = userScore.getUserId();
		this.attack = userScore.getAttack();
		this.defend = userScore.getDefend();
		this.skill = userScore.getSkill();
		this.speed = userScore.getSpeed();
		this.stamina = userScore.getStamina();
		this.strength = userScore.getStrength();
	}
}
