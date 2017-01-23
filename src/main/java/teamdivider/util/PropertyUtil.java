package teamdivider.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.util.StringUtils;

public class PropertyUtil {

  private PropertyUtil() {
    String onlyEmailZonghan = readProperty("EMAIL_ONLY_TO_ZONGHAN");
    if (StringUtils.isEmpty(onlyEmailZonghan)) {
      ONLY_EMAIL_ZONGHAN = true;
    } else {
      ONLY_EMAIL_ZONGHAN = Boolean
          .valueOf(readProperty("EMAIL_ONLY_TO_ZONGHAN"));
    }
  }

  private static String readProperty(String key) {
    String value = "";
    Properties prop = new Properties();
    InputStream in = PropertyUtil.class.getClassLoader()
        .getResourceAsStream("cnf.properties");
    try {
      prop.load(in);
      if (prop.getProperty(key) == null)
        return null;
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

  private final boolean ONLY_EMAIL_ZONGHAN;

  public static boolean emailOnlyToZonghan() {
    return getInstance().ONLY_EMAIL_ZONGHAN;
  }

  public static final int SHOW_EVENTS_NUMBER = 5;

  public enum StringPropertyEnum {
    BASE_LINK("BASE_LINK", "http://127.0.0.1:8080"), AVATAR_BASE_LINK(
        "AVATAR_BASE_LINK",
        "http://7xjao2.com1.z0.glb.clouddn.com/"), AVATAR_BUCKET_NAME(
            "AVATAR_BUCKET_NAME", "sacavatardev");

    StringPropertyEnum(String label, String defaultValue) {
      this.label = label;
      this.defaultValue = defaultValue;
    }

    private String label;

    private String defaultValue;

    private String value = null;

    public String getValue() {
      if (this.value == null) {
        this.value = readProperty(label);
        if (StringUtils.isEmpty(value)) {
          this.value = defaultValue;
        }
      }
      return value;
    }

  }
}