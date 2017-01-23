/*
 * $Id$
 */
package teamdivider.controller.v2;

import java.text.ParseException;
import java.util.Date;

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

import teamdivider.bean.eo.Event;
import teamdivider.bean.eo.Type;
import teamdivider.bean.eo.User;
import teamdivider.dao.TypeDAO;
import teamdivider.dao.UserDAO;
import teamdivider.util.ContextUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TypeControllerTest {

  @Autowired
  private TypeController controller;

  @Autowired
  private UserDAO userDAO;

  @Autowired
  private TypeDAO typeDAO;

  private static Type soccer = null;

  private static User zonghan = null;

  private static User xuejiao = null;

  private static Event event1 = null;

  @Before
  // make sure types added
  public void before() {
    ContextUtil.getContext().skipQiniuActions = true;
    if (zonghan == null) {
      String email = "zonghan.wu@sap.com" + new Date().getTime();
      zonghan = new User();
      zonghan.setEmail(email);
      zonghan.setAvatar("avatar");
      zonghan.setFullName("Zonghan Wu");
      this.userDAO.create(zonghan);
      zonghan = this.userDAO.findByEmail(email);
    }
    if (xuejiao == null) {
      String email = "xuejiao.sun@sap.com" + new Date().getTime();
      xuejiao = new User();
      xuejiao.setEmail(email);
      xuejiao.setAvatar("avatar");
      xuejiao.setFullName("Xuejiao Sun");
      this.userDAO.create(xuejiao);
      xuejiao = this.userDAO.findByEmail(email);
    }
    if (soccer == null) {
      String typeName = "soccer" + new Date().getTime();
      this.controller.addActivityType(typeName, zonghan.getEmail());
      soccer = this.typeDAO.getTypeByName(typeName, true);
    }
  }

  @Test
  public void testA1AddEvent() throws ParseException {
    this.controller.addActivityEvent(soccer.getName(), "event1",
        new Date(), "event1",
        new Date());
    event1 = this.typeDAO.getTypeByTypeId(soccer.getTypeId(), true).getEvents()
        .iterator().next();
  }

  @Test
  public void testA2Organizer() {
    this.controller.becomeOrganizer(soccer.getName(), xuejiao.getEmail());
    this.controller.giveUpOrganizer(soccer.getName(), zonghan.getEmail());
  }

  @Test
  public void testA3Subscriber() {
    this.controller.userSubscribe(soccer.getName(), zonghan.getEmail());
    this.controller.userSubscribe(soccer.getName(), xuejiao.getEmail());
  }

  @Test
  public void testA4Member() {
    this.controller.enrollActivityEvent(soccer.getName(), zonghan.getEmail(),
        event1.getEventId());
    this.controller.enrollActivityEvent(soccer.getName(), xuejiao.getEmail(),
        event1.getEventId());
    this.controller.quitActivityEvent(soccer.getName(), zonghan.getEmail(),
        event1.getEventId());
    this.controller.enrollActivityEvent(soccer.getName(), xuejiao.getEmail(),
        event1.getEventId());
    Assert.assertTrue(
        this.controller.activityEvent(soccer.getName(), event1.getEventId())
            .getMembers().size() == 1);
  }

  @Test
  public void testA5Driver() {
    this.controller.yesDrivingCar(soccer.getName(), zonghan.getEmail(),
        event1.getEventId());
    this.controller.noDrivingCar(soccer.getName(), zonghan.getEmail(),
        event1.getEventId());
  }

  @Test
  public void testA6Passenger() {
    this.controller.yesDrivingCar(soccer.getName(), zonghan.getEmail(),
        event1.getEventId());
    this.controller.byHisCar(soccer.getName(), event1.getEventId(),
        zonghan.getEmail(), xuejiao.getEmail(), false);
    this.controller.notByHisCar(soccer.getName(), event1.getEventId(),
        zonghan.getEmail(), xuejiao.getEmail(), false);
  }
  
  @Test
  public void testA7Guest() {
    this.controller.addGuest("guest1", soccer.getName(), event1.getEventId());
    this.controller.removeGuest("guest1", soccer.getName(), event1.getEventId());
  }
  
  @Test
  public void testA8Query() {
    this.controller.activityEvent(soccer.getName(), event1.getEventId());
    this.controller.activityType(soccer.getName(), false, true);
    this.controller.isUserInCar(soccer.getName(), event1.getEventId(), xuejiao.getEmail());
  }

  @Test
  public void testXDelete() {
    this.controller.deleteActivity(soccer.getName());
    this.userDAO.deleteUser(zonghan.getUserId());
    this.userDAO.deleteUser(xuejiao.getUserId());
  }

  @After
  public void after() {
  }

  @Configuration
  @ComponentScan("teamdivider.*")
  public static class TestConfiguration {
  }
}
