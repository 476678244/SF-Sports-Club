/*
 * $Id$
 */
package teamdivider.bean.vo;

import java.util.ArrayList;
import java.util.List;

import teamdivider.entity.ActivityEvent;

public class EventVO extends ActivityEvent {

  private long eventId;
  
  private List<UserVO> members = new ArrayList<UserVO>();
  
}
