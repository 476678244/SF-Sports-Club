package teamdivider.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.util.StringUtils;

public class PropertyUtil {

  private PropertyUtil(){
    String baseLink = PropertyUtil.readProperty("BASE_LINK");
    if (StringUtils.isEmpty(baseLink)) {
      BASE_LINK = "http://127.0.0.1:8080";
    } else {
      BASE_LINK = baseLink;
    }
  }
  
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

  private static final PropertyUtil instance = new PropertyUtil();
  
  public static PropertyUtil getInstance() {
    return instance;
  }
  
  public final String BASE_LINK;

  public static boolean emailOnlyToZonghan() {
    return Boolean.valueOf(readProperty("EMAIL_ONLY_TO_ZONGHAN"));
  }
  
  public static final String AVATAR_BUCKET_NAME = PropertyUtil
      .readProperty("AVATAR_BUCKET_NAME");

  public static final String AVATAR_BASE_LINK = PropertyUtil
      .readProperty("AVATAR_BASE_LINK");
  
  public static final int SHOW_EVENTS_NUMBER = 5;
}