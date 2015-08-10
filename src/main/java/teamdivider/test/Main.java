package teamdivider.test;

import java.util.ArrayList;
import java.util.List;

import teamdivider.engine.*;
import teamdivider.entity.Team;
import teamdivider.entity.User;

public class Main {

  /**
   * @param args
   */
  public static void main(String[] args) {
    List<User> alljoiners = new ArrayList<User>();
    alljoiners.add(member("", "steven yang", 30));
    alljoiners.add(member("", "xuesong", 25));
    alljoiners.add(member("", "kang wei", 15));
    alljoiners.add(member("", "xiaozhi", 30));
    alljoiners.add(member("", "kenny", 25));
    alljoiners.add(member("", "dewei", 35));
    alljoiners.add(member("", "goddy", 50));
    alljoiners.add(member("", "ken", 40));
    alljoiners.add(member("", "jackie", 25));
    alljoiners.add(member("", "clark", 10));
    alljoiners.add(member("", "shane", 20));
    alljoiners.add(member("", "pal", 15));
    alljoiners.add(member("", "lu ke", 25));
    alljoiners.add(member("", "hong", 25));
    alljoiners.add(member("", "zonghan", 35));
    alljoiners.add(member("", "haoran", 20));

    Fendui engine = new Fendui();
    List<Team> teams = engine.fenDui(2, alljoiners);
    for (Team team : teams) {
      System.out.println();
      System.out.println("Team:");
      for (User member : team.getMembers()) {
        System.out.println(member.getFullname());
      }
    }
    System.out.println(teams);
  }

  static User member(String uname, String fullname, int score) {
    return new User(null, uname, fullname, score);
  }
}
