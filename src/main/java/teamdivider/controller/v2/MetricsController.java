package teamdivider.controller.v2;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import teamdivider.bean.eo.Event;
import teamdivider.bean.eo.Type;
import teamdivider.bean.eo.User;
import teamdivider.bean.eo.mapping.UserScore;
import teamdivider.bean.vo.UserScoreVO;
import teamdivider.dao.EventDAO;
import teamdivider.dao.TypeDAO;
import teamdivider.dao.UserDAO;
import teamdivider.entity.Team;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by wuzonghan on 17/1/30.
 */
@RestController
@RequestMapping("/v2")
public class MetricsController {

	@Autowired
	private TypeController typeController;

	@Autowired
	private TypeDAO typeDAO;

	@Autowired
	private UserDAO userDAO;

	@Autowired
	private EventDAO eventDAO;

	private static Map<String , UserScore> defaultUserScoreMap = Maps.newHashMap();

	@RequestMapping("/metrics")
	public MetricsVO metrics(
		@RequestParam("activityType") String typeName,
		@RequestParam("username") String username) {
		User user = this.userDAO.findByEmail(username);
		Type type = this.typeDAO.getTypeByName(typeName, true);
		int continuousTimes = 0;
		int totalTimes = 0;
		boolean continuousTimesEffective = true;
		boolean isOrganizer = false;
		List<Event> events = type.getEvents().stream().sorted((a, b) -> {
			return a.getEventId() - b.getEventId() < 0 ? 1 : -1;
		}).collect(Collectors.toList());
		if (type.getOrganizers().contains(user))
			isOrganizer = true;
		for (Event event : events) {
			if (this.eventDAO.isMember(event.getEventId(), user)) {
				totalTimes++;
				if (continuousTimesEffective) {
					continuousTimes++;
				}
			} else {
				continuousTimesEffective = false;
			}
		}
		return new MetricsVO(continuousTimes, isOrganizer, totalTimes, new UserScoreVO(findUserScore(username)));
	}

	@Data
	@AllArgsConstructor
	public class MetricsVO {
		private int continuousTimes;
		private boolean isOrganizer;
		private int totalTimes;
		private UserScoreVO userScoreVO;
	}

	static {
		defaultUserScoreMap.put("zonghan.wu@sap.com",
			UserScore.builder().attack(99).defend(99).skill(99).speed(99).stamina(99).strength(99)
				.build());
		defaultUserScoreMap.put("luoxg2001@yahoo.com",
			UserScore.builder().attack(99).defend(99).skill(99).speed(99).stamina(99).strength(99)
				.build());
	}

	public UserScore findUserScore(String username) {
		if (defaultUserScoreMap.containsKey(username)) {
			return defaultUserScoreMap.get(username);
		}
		return UserScore.builder().attack(77).defend(77).skill(77).speed(77).stamina(77).strength(77)
			.build();
	}
}
