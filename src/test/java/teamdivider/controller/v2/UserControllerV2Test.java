package teamdivider.controller.v2;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
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
import teamdivider.dao.UserDAO;
import teamdivider.util.ContextUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserControllerV2Test {

  @Autowired
  private UserControllerV2 controller;
  
  @Autowired
  private UserDAO userDAO;
  
  @Autowired
  private TypeDAO typeDAO;
  
  private static Set<String> emails = new HashSet<String>();
  
  @Before
  // make sure types added
  public void before() {
    ContextUtil.getContext().skipQiniuActions = true;
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
    String email = this.genereateNewEmail();
    String fullName = "Zonghan Wu";
    String avatar = "avatar address";
    UserVO user = this.controller.addUser(email, fullName, avatar);
    Assert.assertEquals(user.getEmail(), email);
    List<UserVO> vos = this.controller.user("all");
    System.out.println("---Users--------------------");
    System.out.println(vos);
    System.out.println("-----------------------");
    Assert.assertTrue(vos.size() > 0);
    emails.add(email);
  }

  @Test
  // delete user and addWithSubscribing
  public void testB() {
    String email = this.genereateNewEmail();
    UserVO vo = this.controller.addUserWithSubscribing(email,
        "Zonghan Wu", "avatar address", "soccer/badminton/");
    Assert.assertEquals(vo.getSubscribedTypes().size(), 2);
    emails.add(email);
  }
  
  @Test
  // update user
  public void testC() {
    String email = this.genereateNewEmail();
    this.controller.addUserWithSubscribing(email,
        "Zonghan Wu", "avatar address", "soccer/badminton/");
    String newEmail = new Date().getTime() + "@163.com";
    User user = this.userDAO.findByEmail(newEmail);
    Assert.assertNull(user);
    this.controller.updateUserEmail(email, newEmail);
    user = this.userDAO.findByEmail(newEmail);
    Assert.assertNotNull(user);
    emails.add(newEmail);
  }
  
  @After
  public void testD() {
    for (String email : emails) {
      this.controller.deleteUser(email);
    }
  }
  
  private String genereateNewEmail() {
    try {
      Thread.sleep(1);
    } catch (InterruptedException e) {
    }
    return new Date().getTime() + "@163.com";
  }
  
  @Configuration
  @ComponentScan("teamdivider.*")
  public static class TestConfiguration{}

}
