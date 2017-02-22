package teamdivider.controller.v2;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.mongodb.util.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import teamdivider.bean.eo.Event;
import teamdivider.bean.eo.Type;
import teamdivider.bean.eo.User;
import teamdivider.bean.eo.UserScore;
import teamdivider.bean.eo.mapping.TypeUserScore;
import teamdivider.bean.vo.UserScoreVO;
import teamdivider.dao.EventDAO;
import teamdivider.dao.TypeDAO;
import teamdivider.dao.TypeUserScoreDAO;
import teamdivider.dao.UserDAO;

import java.io.IOException;
import java.util.HashMap;
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

	@Autowired
	private TypeUserScoreDAO typeUserScoreDAO;

	private static Map<String , UserScore> preConfigUserScoreMap = Maps.newHashMap();

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
		return new MetricsVO(continuousTimes, isOrganizer, totalTimes,
			new UserScoreVO(findUserScore(user.getUserId(), type.getTypeId())));
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
		preConfigUserScoreMap.put("zonghan.wu@sap.com",
			UserScore.builder().attack(99).defend(99).skill(99).speed(99).stamina(99).strength(99)
				.build());
		preConfigUserScoreMap.put("luoxg2001@yahoo.com",
			UserScore.builder().attack(99).defend(99).skill(99).speed(99).stamina(99).strength(99)
				.build());
	}

	UserScore findUserScore(long userId, long typeId) {
		UserScore result = this.typeUserScoreDAO.getUserScore(userId, typeId);
		if (result != null) return result;
		String username = this.userDAO.findByUserId(userId).getEmail();
		if (preConfigUserScoreMap.containsKey(username)) {
			return preConfigUserScoreMap.get(username);
		}
		return UserScore.builder().attack(77).defend(77).skill(77).speed(77).stamina(77).strength(77)
			.build();
	}

	void upsertScore(@RequestParam("username") String username,
		@RequestParam("type") String type,
		@RequestBody Map<String, Integer> scoreMap) {
		UserScore score = UserScore.builder().attack(scoreMap.get("attack"))
			.defend(scoreMap.get("defend")).skill(scoreMap.get("skill"))
			.speed(scoreMap.get("speed")).stamina(scoreMap.get("stamina"))
			.strength(scoreMap.get("strength")).build();
		this.typeUserScoreDAO.upsert(
			TypeUserScore.builder().userId(this.userDAO.findByEmail(username).getUserId())
				.typeId(this.typeDAO.getTypeByName(type).getTypeId()).score(score).build());
	}

	@RequestMapping(value = "/score", method = RequestMethod.POST)
	public void postScore(@RequestParam("username") String username,
		@RequestParam("type") String type,
		@RequestParam("scoreMap") String scoreMap) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Integer> map = mapper.readValue(scoreMap, new TypeReference<Map<String, Integer>>(){});
		this.upsertScore(username, type, map);
	}
}
