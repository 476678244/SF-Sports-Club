package teamdivider.util;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import teamdivider.controller.v2.TypeController;

/**
 * Created by wuzonghan on 17/2/21.
 */
public class TestSupport {

	protected final Logger log = Logger.getLogger(this.getClass());

	private static final String testType = "testType";

	private static final String testUser = "a476678244@gmail.com";

	@Autowired
	protected TypeController typeController;

	@Before
	public void prepare() {
		typeController.addActivityType(testType, testUser);
	}

	@After
	public void cleanup() {
		typeController.deleteActivity(testType);
	}


}
