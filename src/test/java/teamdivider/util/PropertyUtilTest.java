package teamdivider.util;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class PropertyUtilTest {

  private static final Logger log = Logger.getLogger(IPGeneratorTest.class);

  @Test
  public void test() {
    log.info(
        "BASE_LINK " + PropertyUtil.StringPropertyEnum.BASE_LINK.getValue());
    log.info("AVATAR_BASE_LINK "
        + PropertyUtil.StringPropertyEnum.AVATAR_BASE_LINK.getValue());
    log.info("AVATAR_BUCKET_NAME "
        + PropertyUtil.StringPropertyEnum.AVATAR_BUCKET_NAME.getValue());
    log.info("SHOW_EVENTS_NUMBER " + PropertyUtil.SHOW_EVENTS_NUMBER);
    log.info("email only to zonghan " + PropertyUtil.emailOnlyToZonghan());
  }

  @Configuration
  @ComponentScan("teamdivider.*")
  public static class TestConfiguration {
  }
}
