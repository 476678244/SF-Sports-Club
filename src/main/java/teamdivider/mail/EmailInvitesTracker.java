package teamdivider.mail;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EmailInvitesTracker {

  private static Map<Long, Date> event2TimeMap = new ConcurrentHashMap<Long, Date>();

  public static boolean getApproval(Long eventId) {
    Date latestTime = event2TimeMap.get(eventId);
    if (latestTime == null) {
      event2TimeMap.put(eventId, new Date());
      return true;
    } else {
      if ((event2TimeMap.get(eventId).getTime() - new Date().getTime()) > 10
          * 1000 * 60) {
        event2TimeMap.put(eventId, new Date());
        return true;
      } else {
        return false;
      }
    }
  }

  public static void clear() {
    event2TimeMap.clear();
  }

  public static Map<Long, Date> status() {
    return Collections.unmodifiableMap(event2TimeMap);
  }
}
