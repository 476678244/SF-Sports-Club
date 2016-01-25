package teamdivider.controller.v2;

import java.util.Date;
import java.util.List;

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
import org.apache.log4j.Logger;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserControllerV2Test {

  private static final Logger log = Logger.getLogger(UserControllerV2Test.class);
  
  @Autowired
  private UserControllerV2 controller;

  @Autowired
  private UserDAO userDAO;

  @Autowired
  private TypeDAO typeDAO;

  private static User zonghan = null;

  private static Type soccer = null;

  private static Type badminton = null;

  @Before
  // make sure types added
  public void before() {
    ContextUtil.getContext().skipQiniuActions = true;
    if (soccer == null) {
      String typeName = "soccer" + new Date().getTime();
      Type type = Type.builder().name(typeName).build();
      this.typeDAO.create(type);
      soccer = this.typeDAO.getTypeByName(typeName, true);
    }
    if (badminton == null) {
      String typeName = "badminton" + new Date().getTime();
      Type type = Type.builder().name(typeName).build();
      this.typeDAO.create(type);
      badminton = this.typeDAO.getTypeByName(typeName, true);
    }
  }

  @Test
  // get users and add users
  public void testA2() {
    String email = this.genereateNewEmail();
    String fullName = "Zonghan Wu";
    String avatar = "avatar address";
    UserVO user = this.controller.addUser(email, fullName, avatar);
    zonghan = this.userDAO.findByEmail(email);
    Assert.assertEquals(user.getEmail(), email);
    List<UserVO> vos = this.controller.user("all");
    log.info("---Users--------------------");
    log.info(vos);
    log.info("-----------------------");
    Assert.assertTrue(vos.size() > 0);
  }

  @Test
  // delete user and addWithSubscribing
  public void testB() {
    this.controller.deleteUser(zonghan.getEmail());
    String email = this.genereateNewEmail();
    UserVO vo = this.controller.addUserWithSubscribing(email, "Zonghan Wu",
        "avatar address", "soccer/badminton/");
    Assert.assertEquals(vo.getSubscribedTypes().size(), 2);
    zonghan = this.userDAO.findByEmail(vo.getEmail());
  }

  @Test
  // update user
  public void testC() {
    String newEmail = genereateNewEmail();
    this.controller.updateUserEmail(zonghan.getEmail(), newEmail);
    zonghan = this.userDAO.findByEmail(newEmail);
    Assert.assertNotNull(zonghan);
  }

  @Test
  public void testXDelete() {
    this.typeDAO.deleteType(soccer.getTypeId());
    this.typeDAO.deleteType(badminton.getTypeId());
    this.userDAO.deleteUser(zonghan.getUserId());
  }
  
  @After
  public void after() {
    
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
  public static class TestConfiguration {
  }

}
