package teamdivider.repo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import teamdivider.entity.ActivityEvent;
import teamdivider.entity.ActivityType;
import teamdivider.entity.EntityUtil;
import teamdivider.util.CacheUtil;

@Repository
public class ActivityTypeDAO {

  @Autowired
  private Datastore datastore;
  
  public List<ActivityType> getAllActivityTypes() {
    List<ActivityType> types = datastore.find(ActivityType.class).asList();
    return types;
  }
  
  public List<ActivityType> getAllActivityTypes(boolean tryCache) {
    if (tryCache) {
      Set<String> allKindTypes = this.getAllKindTypes();
      List<ActivityType> types = new ArrayList<ActivityType>();
      for (String kindType : allKindTypes) {
        types.add(this.getActivityTypeByName(kindType, true));
      }
      return types;
    } else {
      return this.getAllActivityTypes();
    }
  }
  
  public Set<String> getAllKindTypes() {
    Set<String> names = CacheUtil.getTypeNamesCache();
    if (names == null) {
      List<ActivityType> types = this.getAllActivityTypes();
      names = new HashSet<String>();
      for (ActivityType type : types) {
        names.add(type.getName());
      }
      CacheUtil.addToTypeNamesCache(names);
      return names;
    } else {
      return names;
    }
  }
  
  public ActivityType saveActivityType(ActivityType type) {
    this.datastore.save(type);
    CacheUtil.removeTypeCache(type.getName());
    return type;
  }
  
  public ActivityType getActivityTypeById(ObjectId id) {
    return datastore.get(ActivityType.class, id);
  }
  
  public ActivityType getActivityTypeByName(String name) {
    return this.getActivityTypeByName(name, true);
  }

  private ActivityType getActivityTypeByName(String name, boolean tryCache) {
    if (!tryCache) {
      return getTypeByNameFromDB(name);
    }
    ActivityType cache = CacheUtil.getTypeFromCache(name);
    if (cache == null) {
      ActivityType type = getTypeByNameFromDB(name);
      CacheUtil.addToTypeCache(name, type);
      return type;
    } else {
      return cache;
    }
  }
  
  public ActivityType getTypeByNameFromDB(String name) {
    ActivityType type = datastore.find(ActivityType.class, "name", name).get();
    if (type == null) return null;
    EntityUtil.sortEventByOrdinalDesc(type.getEvents());
    return type;
  }
  
  public ActivityEvent getActivityEventByTypeOrdinal(String type, int ordinal) {
    ActivityType activityType = this.getActivityTypeByName(type);
    return activityType.getEventByOrdinal(ordinal);
  }
  
  public void deleteActivityType(ActivityType type) {
    this.datastore.delete(type);
  }

  public void deleteAll() {
    datastore.delete(datastore.createQuery(ActivityType.class));
  }
}
