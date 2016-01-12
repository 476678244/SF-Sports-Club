package teamdivider.repo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import teamdivider.entity.ActivityEvent;
import teamdivider.entity.ActivityType;
import teamdivider.entity.User;

@Repository
public class BaseData {

  public static String EXAMPLE_ACTIVITY_TYPE = "soccer";

  @Autowired
  private UserDAO userDAO;
  
  @Autowired
  private ActivityTypeDAO activityTypeDAO;
  
  public static List<User> getBasicUsers() {
    List<User> basicUsers = new ArrayList<User>();
    basicUsers.add(new User("zonghan.wu@sap.com", "Zonghan Wu", "avatar/zonghan.jpg", 1));
    basicUsers.add(new User("xuesong.luo@sap.com", "Xuesong Luo", "avatar/xuesong.jpg", 1));
    basicUsers.add(new User("yulong.yang@sap.com", "Steven Yang", "avatar/stevenYang.jpg", 1));
    basicUsers.add(new User("xiao.zhi.yan@sap.com", "XiaoZhi Yan", "avatar/xiaozhi.jpg", 1));
    basicUsers.add(new User("xiao.han@sap.com", "Han Xiao", "avatar/hanxiao.jpg", 1));
    basicUsers.add(new User("edwin.liu@sap.com", "Edwin Liu", "avatar/dewei.jpg", 1));
    basicUsers.add(new User("ke.lu01@sap.com", "Lu Ke", "avatar/luke.jpg", 1));
    basicUsers.add(new User("mark.feng@sap.com", "Mark Feng", "avatar/mark.jpg", 1));
    basicUsers.add(new User("nick.luo@sap.com", "Nick Luo", "avatar/nick.jpg", 1));
    basicUsers.add(new User("yonggao.pan@sap.com", "Yonggao Pan", "avatar/pan.jpg", 1));
    basicUsers.add(new User("simic.zhang@sap.com", "Simic Zhang", "avatar/simic.jpg", 1));
    basicUsers.add(new User("steven.weng@sap.com", "Steven Weng", "avatar/stevenWeng.jpg", 1));
    basicUsers.add(new User("kun.liu01@sap.com", "Wright Liu", "avatar/wright.jpg", 1));
    basicUsers.add(new User("goddy.zhao@sap.com", "Goddy Zhao", "avatar/goddy.jpg", 1));
    basicUsers.add(new User("ken.feng@sap.com", "Ken Feng", "avatar/ken.jpg", 1));
    basicUsers.add(new User("fan.yang02@sap.com", "Fred Yang", "avatar/fred.jpg", 1));
    basicUsers.add(new User("yueling.wang@sap.com", "Yueling Wang", "avatar/yuelingwang.jpg", 1));
    basicUsers.add(new User("xiaoqiu.xu@sap.com", "Kenny Xu", "avatar/kenny.jpg", 1));
    basicUsers.add(new User("jackie.f.cheng@gmail.com", "Jackie Cheng", "avatar/messi.jpg", 1));
    basicUsers.add(new User("shane.wang01@sap.com", "Shane Wang", "avatar/shanewang.jpg", 1));
    basicUsers.add(new User("wei.kang02@sap.com", "Kang Wei", "avatar/kangwei.jpg", 1));
    basicUsers.add(new User("yaming.zhao@sap.com", "Yaming Zhao", "avatar/yamingzhao.jpg", 1));
    basicUsers.add(new User("hua.xie@sap.com", "Howard Xie", "avatar/howardxie.jpg", 1));
    basicUsers.add(new User("basten.zhu@sap.com", "Basten Zhu", "avatar/basten.jpg", 1));
    basicUsers.add(new User("lang.zongming@sap.com", "Clark Lang", "avatar/clark.jpg", 1));
    basicUsers.add(new User("roy.zhang02@sap.com", "Roy Zhang", "avatar/royzhang.jpg", 1));
    basicUsers.add(new User("haoran.feng@sap.com", "Haoran Feng", "avatar/haoranfeng.jpg", 1));
    basicUsers.add(new User("la.you@sap.com", "You La", "avatar/youla.jpg", 1));
    basicUsers.add(new User("andy.wu01@sap.com", "Andy Wu", "avatar/andy.jpg", 1));
    basicUsers.add(new User("wei.zhang01@sap.com", "Zhang Wei", "avatar/zhangwei.jpg", 1));
    basicUsers.add(new User("dawei.xing@sap.com", "Dawei Xing", "avatar/dawei.jpg", 1));
    basicUsers.add(new User("ozzy.deng@sap.com", "Ozzy Deng", "avatar/ozzy.jpg", 1));
    return basicUsers;
  }

  public static Map<ObjectId, Integer> getActivitiesScoreStatistic(UserDAO userDAO) {
    Map<ObjectId, Integer> soccerScoreStatistic = new HashMap<ObjectId, Integer>();
    soccerScoreStatistic.put(userDAO.findByUsername("zonghan.wu@sap.com").getId(), 8);
    soccerScoreStatistic.put(userDAO.findByUsername("xuesong.luo@sap.com").getId(), 6);
    soccerScoreStatistic.put(userDAO.findByUsername("yulong.yang@sap.com").getId(), 6);
    soccerScoreStatistic.put(userDAO.findByUsername("xiao.zhi.yan@sap.com").getId(), 6);
    soccerScoreStatistic.put(userDAO.findByUsername("xiao.han@sap.com").getId(), 8);
    soccerScoreStatistic.put(userDAO.findByUsername("edwin.liu@sap.com").getId(), 8);
    soccerScoreStatistic.put(userDAO.findByUsername("ke.lu01@sap.com").getId(), 6);
    soccerScoreStatistic.put(userDAO.findByUsername("mark.feng@sap.com").getId(), 5);
    soccerScoreStatistic.put(userDAO.findByUsername("nick.luo@sap.com").getId(), 3);
    soccerScoreStatistic.put(userDAO.findByUsername("yonggao.pan@sap.com").getId(), 8);
    soccerScoreStatistic.put(userDAO.findByUsername("simic.zhang@sap.com").getId(), 3);
    soccerScoreStatistic.put(userDAO.findByUsername("steven.weng@sap.com").getId(), 4);
    soccerScoreStatistic.put(userDAO.findByUsername("goddy.zhao@sap.com").getId(), 10);
    soccerScoreStatistic.put(userDAO.findByUsername("ken.feng@sap.com").getId(), 9);
    soccerScoreStatistic.put(userDAO.findByUsername("kun.liu01@sap.com").getId(), 5);
    soccerScoreStatistic.put(userDAO.findByUsername("fan.yang02@sap.com").getId(), 5);
    soccerScoreStatistic.put(userDAO.findByUsername("yueling.wang@sap.com").getId(), 4);
    soccerScoreStatistic.put(userDAO.findByUsername("xiaoqiu.xu@sap.com").getId(), 6);
    soccerScoreStatistic.put(userDAO.findByUsername("jackie.f.cheng@gmail.com").getId(), 6);
    soccerScoreStatistic.put(userDAO.findByUsername("shane.wang01@sap.com").getId(), 5);
    soccerScoreStatistic.put(userDAO.findByUsername("wei.kang02@sap.com").getId(), 5);
    soccerScoreStatistic.put(userDAO.findByUsername("yaming.zhao@sap.com").getId(), 6);
    soccerScoreStatistic.put(userDAO.findByUsername("hua.xie@sap.com").getId(), 4);
    soccerScoreStatistic.put(userDAO.findByUsername("basten.zhu@sap.com").getId(), 7);
    soccerScoreStatistic.put(userDAO.findByUsername("lang.zongming@sap.com").getId(), 3);
    soccerScoreStatistic.put(userDAO.findByUsername("roy.zhang02@sap.com").getId(), 4);
    soccerScoreStatistic.put(userDAO.findByUsername("haoran.feng@sap.com").getId(), 6);
    soccerScoreStatistic.put(userDAO.findByUsername("la.you@sap.com").getId(), 7);
    soccerScoreStatistic.put(userDAO.findByUsername("andy.wu01@sap.com").getId(), 3);
    soccerScoreStatistic.put(userDAO.findByUsername("wei.zhang01@sap.com").getId(), 7);
    soccerScoreStatistic.put(userDAO.findByUsername("dawei.xing@sap.com").getId(), 3);
    soccerScoreStatistic.put(userDAO.findByUsername("ozzy.deng@sap.com").getId(), 3);
    return soccerScoreStatistic;
  }
  
  public ActivityType addExampleActivityType() {
    ActivityType soccer = new ActivityType(EXAMPLE_ACTIVITY_TYPE,
        this.userDAO.findByUsername("zonghan.wu@sap.com"));
    soccer.setRegularTime("Tuesday 6:00 pm");
    int ordinal = soccer.getLatestOrdinal() + 1;

    ActivityEvent event = new ActivityEvent(ordinal, "周二张江中学踢球(1/27)",
        soccer.getRegularTime(), new Date());
    event.setDescription("start to go @5:30pm");
    event.setType(soccer.getName());

    List<User> soccerMembers = new ArrayList<User>();
    soccerMembers.add(this.userDAO.findByUsername("zonghan.wu@sap.com"));
    event.getMembers().addAll(soccerMembers);
    soccer.addEvent(event);
    Map<ObjectId, Integer> statistic = getActivitiesScoreStatistic(this.userDAO);
    soccer.setScoreStatistic(statistic);
    return this.activityTypeDAO.saveActivityType(soccer);
  }
  
  public ActivityType saveExampleActivityType(ActivityType type) {
    return this.activityTypeDAO.saveActivityType(type);
  }
  
  public ActivityType installSoccer() {
    ActivityType soccer = getSoccer();
    if (soccer == null) {
      return this.addExampleActivityType();
    }
    return soccer;
  }

  public void mergeUsers() {
    List<User> basicUsers = getBasicUsers();
    for (User user : basicUsers) {
       User userInDB = this.findByUsername(user.getUsername());
       if (userInDB == null) {
         this.userDAO.saveUser(user);
       }
    }
  }
  
  User findByUsername(String username) {
    return this.userDAO.findByUsername(username);
  }
  
  public ActivityEvent getExampleEvent(ActivityType soccer) {
    return soccer.getEvents().get(0);
  }
  
  public void clearDB() {
    this.activityTypeDAO.deleteAll();
    this.userDAO.deleteAll();
  }
  
  public ActivityType getSoccer() {
    return this.activityTypeDAO.getActivityTypeByName(EXAMPLE_ACTIVITY_TYPE);
  }
}
