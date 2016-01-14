/*
 * $Id$
 */
package teamdivider.bean.vo;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import teamdivider.bean.eo.Type;
import teamdivider.entity.User;
import teamdivider.util.ContextUtil;

public class UserVO extends User {

  private long userId;

  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

  public Set<String> getSubscribedTypes() {
    if (!ContextUtil.getContext().fetchUserSubscribedTypes)
      return Collections.emptySet();
    Set<String> types = new HashSet<String>();
    for (Type type : ContextUtil.getContext().getUserMapping(this.userId)
        .getSubscribedTypes()) {
      types.add(type.getName());
    }
    return types;
  }
}
