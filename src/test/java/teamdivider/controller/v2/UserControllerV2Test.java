package teamdivider.controller.v2;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class UserControllerV2Test {

  @Autowired
  private UserControllerV2 controller;

  @Test
  public void test() {
    String email = "zonghan_ishare@163.com";
    String fullName = "Zonghan Wu";
    String avatar = "avatar address";
    this.controller.addUser(email, fullName, avatar);
  }
  
  @Configuration
  @ComponentScan("teamdivider.*")
  public static class TestConfiguration{}

}
