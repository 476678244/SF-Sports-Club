package teamdivider.util;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import teamdivider.entity.ActivityType;

public class CacheUtil {

  private static Map<String, TypeCache> typesCache = new HashMap<String, TypeCache>();

  private static TypeNamesCache typeNamesCache = null;

  public static Set<String> getTypeNamesCache() {
    if (ContextUtil.getContext().disableCache) {
      return null;
    }
    if (typeNamesCache == null)
      return null;
    Set<String> cache = typeNamesCache.getNames();
    long duration = new Date().getTime() - typeNamesCache.getDate().getTime();
    if (duration > 1000 * 60 * 60 * 12) {
      removeTypeNamesCache();
      return null;
    } else {
      return cache;
    }
  }
  
  /**
   * to be used as synchronied lock
   */
  public static String getInCacheTypeString(String type) {
    if (ContextUtil.getContext().disableCache) {
      return null;
    }
    Set<String> cache = getTypeNamesCache();
    if (cache == null) return type;
    for (String value : cache) {
      if (value.equals(type)) {
        return value;
      }
    }
    return type;
  }

  public static void removeTypeNamesCache() {
    typeNamesCache = null;
  }

  public static void addToTypeNamesCache(Set<String> names) {
    typeNamesCache = new TypeNamesCache(names, new Date());
  }

  public static ActivityType getTypeFromCache(String type) {
    if (ContextUtil.getContext().disableCache) {
      return null;
    }
    if (typesCache.get(type) == null) {
      return null;
    }
    TypeCache cache = typesCache.get(type);
    if (cache != null) {
      long duration = new Date().getTime()
          - typesCache.get(type).getDate().getTime();
      if (duration > 1000 * 5 * 60) {
        removeTypeCache(type);
        return null;
      } else {
        return cache.getValue();
      }
    } else {
      return null;
    }
  }

  public static void removeTypeCache(String type) {
    typesCache.remove(type);
  }

  public static void addToTypeCache(String name, ActivityType type) {
    typesCache.put(name, new TypeCache(type, new Date()));
  }

  public static class TypeCache {
    private ActivityType value;
    private Date date;

    public TypeCache(ActivityType value, Date date) {
      super();
      this.value = value;
      this.date = date;
    }

    public ActivityType getValue() {
      return value;
    }

    public void setValue(ActivityType value) {
      this.value = value;
    }

    public Date getDate() {
      return date;
    }

    public void setDate(Date date) {
      this.date = date;
    }
  }

  public static class TypeNamesCache {
    private Set<String> names = new HashSet<String>();
    private Date date;

    public TypeNamesCache(Set<String> names, Date date) {
      super();
      this.names = names;
      this.date = date;
    }

    public Set<String> getNames() {
      return names;
    }

    public void setNames(Set<String> names) {
      this.names = names;
    }

    public Date getDate() {
      return date;
    }

    public void setDate(Date date) {
      this.date = date;
    }
  }
  
  public static Map<String, TypeCache> printTypesCache() {
    return typesCache;
  }

  public static TypeNamesCache printTypeNamesCache() {
    return typeNamesCache;
  }
}
