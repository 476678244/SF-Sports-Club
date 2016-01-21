package teamdivider.controller.v2;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import teamdivider.dao.EventDAO;
import teamdivider.dao.TypeDAO;
import teamdivider.dao.UserDAO;
import teamdivider.entity.Team;

@RestController
@RequestMapping("/v2")
public class ScoreControllerV2 {

  @Autowired
  private UserDAO userDAO;

  @Autowired
  private TypeDAO typeDAO;

  @Autowired
  private EventDAO eventDAO;
  
  @RequestMapping("/fendui")
  public List<Team> fendui(
      @RequestParam(value = "numberTeams", defaultValue = "2") int numberTeams,
      @RequestParam(value = "reFenDui", defaultValue = "false") boolean reFenDui,
      @RequestParam(value = "username", defaultValue = "") String username,
      @RequestParam(value = "eventId", defaultValue = "0") int eventId) {
    return Collections.emptyList();
  }
}
