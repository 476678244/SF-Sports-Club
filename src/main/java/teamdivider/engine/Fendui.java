package teamdivider.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import teamdivider.entity.ActivityEvent;
import teamdivider.entity.ActivityType;
import teamdivider.entity.EntityUtil;
import teamdivider.entity.Team;
import teamdivider.entity.User;
import teamdivider.util.PropertyUtil;

public class Fendui {

  public static List<Team> fenDui(ActivityType type, ActivityEvent event,
      int numberTeams) {
    // construct temp user with score
    List<User> allJoiners = event.getMembers();
    for (User joiner : allJoiners) {
      int realScore = type.getUserScore(joiner.getUsername()) == 0 ? PropertyUtil.DEFAULT_USER_SCORE
          : type.getUserScore(joiner.getUsername());
      joiner.setScore(realScore);
    }
    return new Fendui().fenDuiIncludingGuests(numberTeams, allJoiners,
        event.getGuests());
  }

  // main
  public List<Team> fenDui(int numberTeams, List<User> allJoiners) {
    int numberTry = 3;
    List<List<Team>> results = new ArrayList<List<Team>>(numberTry);
    while (numberTry > 0) {
      results.add(randomFenDui(numberTeams, allJoiners));
      numberTry--;
    }
    return getBestResult(results);
  }

  public List<Team> fenDuiIncludingGuests(int numberTeams,
      List<User> allJoiners, List<String> guests) {
    List<Team> teams = this.fenDui(numberTeams, allJoiners);
    for (String guest : guests) {
      Team weakestTeam = EntityUtil.weakestTeam(teams);
      weakestTeam.addGuest(guest);
    }
    return teams;
  }

  public List<Team> randomFenDui(int numberTeams, List<User> allJoiners) {
    int numberRemainder = allJoiners.size() % numberTeams;
    // remainder number
    List<User> mainJoiners = new ArrayList<User>();
    List<User> remainderJoiners = new ArrayList<User>();
    departIntoRemainderJoinersAndMainJoiners(allJoiners, mainJoiners,
        remainderJoiners, numberRemainder);
    List<Team> departedTeams = departMainJoinrsToTeams(mainJoiners, numberTeams);
    int index = 0; // add highest score remainerJoiners to lowest score
    // teams
    sortJoinersByScoreDesc(remainderJoiners);
    sortTeamsByScoreAsc(departedTeams);
    for (User remainderJoiner : remainderJoiners) {
      departedTeams.get(index).getMembers().add(remainderJoiner);
      departedTeams.get(index).setScore(
          departedTeams.get(index).getScore() + remainderJoiner.getSimpleScore());
      index++;
    }
    return departedTeams;
  }

  public void departIntoRemainderJoinersAndMainJoiners(List<User> allJoiners,
      List<User> mainJoiners, List<User> remainderJoiners, int numberRemainder) {
    sortJoinersByScoreAsc(allJoiners);
    for (User joiner : allJoiners) {
      if (numberRemainder > 0) {
        remainderJoiners.add(joiner);
        numberRemainder--;
      } else {
        mainJoiners.add(joiner);
      }
    }
  }

  public List<Team> departMainJoinrsToTeams(List<User> mainJoiners,
      int numberTeams) {
    int numberJoiners = mainJoiners.size();
    int numberLayers = numberJoiners / numberTeams;
    List<List<User>> layers = departMainJoinrsToLayers(mainJoiners,
        numberTeams, numberLayers);
    List<Team> result = new ArrayList<Team>(numberTeams);
    for (int i = 0; i < numberTeams; i++) {
      Team team = new Team();
      team.setName(String.valueOf(i));
      for (List<User> layer : layers) {
        User randomLayerJoiner = randomGrabAJoinerFromLayer(layer);
        team.getMembers().add(randomLayerJoiner);
        team.setScore(team.getScore() + randomLayerJoiner.getSimpleScore());
      }
      result.add(team);
    }
    return result;
  }

  public User randomGrabAJoinerFromLayer(List<User> layer) {
    int numberJoinersPerLayer = layer.size();
    Random rand = new Random();
    int randomJoinerIndex = rand.nextInt(numberJoinersPerLayer);
    User randomJoiner = layer.get(randomJoinerIndex);
    layer.remove(randomJoiner);
    return randomJoiner;
  }

  List<List<User>> departMainJoinrsToLayers(List<User> mainJoiners,
      int numberTeams, int numberLayers) {
    List<List<User>> layers = new ArrayList<List<User>>();
    sortJoinersByScoreDesc(mainJoiners);
    int index = 0;
    for (int i = 0; i < numberLayers; i++) {
      int numberJoinersLeftPerLayer = numberTeams;
      List<User> layer = new ArrayList<User>();
      while (numberJoinersLeftPerLayer > 0) {
        layer.add(mainJoiners.get(index));
        numberJoinersLeftPerLayer--;
        index++;
      }
      layers.add(layer);
    }
    return layers;
  }

  List<Team> getBestResult(List<List<Team>> allFenDuiResults) {
    List<Team> bestResult = allFenDuiResults.get(0);
    for (List<Team> result : allFenDuiResults) {
      if (getTotalOddsValue(result) < getTotalOddsValue(bestResult)) {
        bestResult = result;
      }
    }
    return bestResult;
  }

  int getTotalOddsValue(List<Team> teams) {
    if (teams.size() == 2) {
      return Math.abs(teams.get(0).getScore() - teams.get(1).getScore());
    } else {
      LinkedList<Team> rightTeams = new LinkedList<Team>();
      rightTeams.addAll(teams);
      Team leftTeam = rightTeams.removeFirst();
      int totalOddsValue = 0;
      for (Team team : rightTeams) {
        totalOddsValue += Math.abs(leftTeam.getScore() - team.getScore());
      }
      return totalOddsValue + getTotalOddsValue(rightTeams);
    }
  }

  void sortJoinersByScoreAsc(List<User> joiners) {
    Collections.sort(joiners, new Comparator<User>() {
      public int compare(User joiner1, User joiner2) {
        return new Integer(joiner1.getScore()).compareTo(new Integer(joiner2
            .getScore()));
      }
    });
  }

  void sortJoinersByScoreDesc(List<User> joiners) {
    Collections.sort(joiners, new Comparator<User>() {
      public int compare(User joiner1, User joiner2) {
        return new Integer(joiner2.getScore()).compareTo(new Integer(joiner1
            .getScore()));
      }
    });
  }

  void sortTeamsByScoreAsc(List<Team> teams) {
    Collections.sort(teams, new Comparator<Team>() {
      public int compare(Team team1, Team team2) {
        return new Integer(team1.getScore()).compareTo(team2.getScore());
      }
    });
  }

}
