package teamdivider.controller.v2;

import java.util.ArrayList;
import java.util.List;

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

import teamdivider.bean.eo.Type;
import teamdivider.bean.eo.User;
import teamdivider.bean.vo.UserVO;
import teamdivider.dao.TypeDAO;
import teamdivider.entity.EntityUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserControllerV2Test {

  @Autowired
  private UserControllerV2 controller;
  
  @Autowired
  private TypeDAO typeDAO;
  
  @Test
  // make sure types added
  public void testA1() {
    Type soccer = this.typeDAO.getTypeByName("soccer", false);
    if (soccer == null) {
      soccer = Type.builder().name("soccer").build();
      this.typeDAO.create(soccer);
    }
    Type badminton = this.typeDAO.getTypeByName("badminton", false);
    if (badminton == null) {
      badminton = Type.builder().name("badminton").build();
      this.typeDAO.create(soccer);
    }
  }

  @Test
  // get users and add users
  public void testA2() {
    List<UserVO> userVOs = this.controller.user("all");
    List<User> eos = new ArrayList<User>();
    if (userVOs != null) {
      for (UserVO vo : userVOs) {
        eos.add(EntityUtil.userOf(vo));
      }
    }
    if (eos.isEmpty()) {
      String email = "zonghan_ishare@163.com";
      String fullName = "Zonghan Wu";
      String avatar = "avatar address";
      UserVO user = this.controller.addUser(email, fullName, avatar);
      eos.add(EntityUtil.userOf(user));
    }
    System.out.println("---Users--------------------");
    System.out.println(eos);
    System.out.println("-----------------------");
    Assert.assertTrue(eos.size() > 0);
  }

  @Test
  // delete user and addWithSubscribing
  public void testB() {
    this.controller.deleteUser("zonghan_ishare@163.com");
    UserVO vo = this.controller.addUserWithSubscribing("zonghan_ishare@163.com",
        "Zonghan Wu", "avatar address", "soccer/badminton/");
    Assert.assertEquals(vo.getSubscribedTypes().size(), 2);
  }
  
  @Configuration
  @ComponentScan("teamdivider.*")
  public static class TestConfiguration{}

}
