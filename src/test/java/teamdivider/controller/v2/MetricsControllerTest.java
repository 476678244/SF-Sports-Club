package teamdivider.controller.v2;

import com.google.common.collect.Maps;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import teamdivider.bean.eo.UserScore;
import teamdivider.util.TestSupport;

import java.util.Map;

/**
 * Created by wuzonghan on 17/2/21.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MetricsControllerTest extends TestSupport {

	@Autowired
	private MetricsController controller;

	private static Map<String, Integer> scoreMap = Maps.newHashMap();

	static {
		scoreMap.put("attack", 91);
		scoreMap.put("defend", 91);
		scoreMap.put("skill", 91);
		scoreMap.put("speed", 91);
		scoreMap.put("stamina", 91);
		scoreMap.put("strength", 91);
	}

	@Test
	public void test() {
		controller.upsertScore(testUser, testType, scoreMap);
		UserScore score = controller.findUserScore(userControllerV2.user(testUser).get(0).getUserId(),
			typeController.activityType(testType, false , false).get(0).getTypeId());
		Assert.assertTrue(score.getAttack() == 91);
	}

	@Configuration
	@ComponentScan("teamdivider.*")
	public static class TestConfiguration {
	}

}
