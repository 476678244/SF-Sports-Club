package teamdivider.util;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import teamdivider.controller.v2.TypeController;
import teamdivider.controller.v2.UserControllerV2;

/**
 * Created by wuzonghan on 17/2/21.
 */
public class TestSupport {

	protected final Logger log = Logger.getLogger(this.getClass());

	protected static final String testType = "testType";

	protected static final String testUser = "a476678244@gmail.com";

	@Autowired
	protected TypeController typeController;

	@Autowired
	protected UserControllerV2 userControllerV2;

	@Before
	public void prepare() throws Exception {
		this.cleanup();
		userControllerV2.addUser(testUser, "Gmail User", "avatar address");
		typeController.addActivityType(testType, testUser);
	}

	@After
	public void cleanup() {
		typeController.deleteActivity(testType);
		userControllerV2.deleteUser(testUser);
	}


}
