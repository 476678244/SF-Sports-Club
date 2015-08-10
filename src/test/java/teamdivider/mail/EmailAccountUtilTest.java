package teamdivider.mail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import teamdivider.repo.UserDAO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class EmailAccountUtilTest {

  @Autowired
  private MailService mailService;

  @Test
  public void testGetAddress() {
    for (int i = 0; i < 1000; i++) {
      EmailAccountUtil.getAddress();
    }
  }
  
  @Configuration
  @ComponentScan("teamdivider.*")
  public static class TestConfiguration{}
}
