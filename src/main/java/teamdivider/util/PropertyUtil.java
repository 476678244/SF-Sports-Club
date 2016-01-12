package teamdivider.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyUtil {

  public static String readProperty(String key) {
    String value = "";
    Properties prop = new Properties();
    InputStream in = PropertyUtil.class.getClassLoader().getResourceAsStream(
        "cnf.properties");
    try {
      prop.load(in);
      value = prop.getProperty(key).trim();
      value = new String(value.getBytes("ISO8859-1"), "UTF-8");
    } catch (IOException e) {
    }
    return value;
  }

  public static final String BASE_LINK = PropertyUtil.readProperty("BASE_LINK");
  
  public static final int DEFAULT_USER_SCORE = Integer.valueOf(PropertyUtil
      .readProperty("DEFAULT_USER_SCORE"));

  public static boolean emailOnlyToZonghan() {
    return Boolean.valueOf(readProperty("EMAIL_ONLY_TO_ZONGHAN"));
  }
  
  public static final String AVATAR_BUCKET_NAME = PropertyUtil
      .readProperty("AVATAR_BUCKET_NAME");

  public static final String AVATAR_BASE_LINK = PropertyUtil
      .readProperty("AVATAR_BASE_LINK");
  
  public static final int SHOW_EVENTS_NUMBER = 5;
  
  public static final boolean DISABLE_CACHE = Boolean
      .valueOf(readProperty("DISABLE_CACHE"));
}