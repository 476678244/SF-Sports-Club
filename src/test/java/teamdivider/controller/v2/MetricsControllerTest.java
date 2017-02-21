package teamdivider.controller.v2;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import teamdivider.util.TestSupport;

/**
 * Created by wuzonghan on 17/2/21.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MetricsControllerTest extends TestSupport {

	@Autowired
	private MetricsController controller;

	@Test
	public void test() {
		log.info(typeController.activityTypes());
	}

	@Configuration
	@ComponentScan("teamdivider.*")
	public static class TestConfiguration {
	}

}
