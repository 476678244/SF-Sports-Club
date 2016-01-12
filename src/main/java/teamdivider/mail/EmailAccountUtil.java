package teamdivider.mail;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class EmailAccountUtil {

  private static final LinkedList<String> addresses = new LinkedList<String>();

  private static final Map<String, String> addressPasswordMap = new HashMap<String, String>();

  private static final LinkedList<String> template = new LinkedList<String>();

  static {
    template.add("a476678244@sina.cn");
    template.add("a4766782441@sina.cn");
    template.add("a4766782442@sina.cn");
    template.add("a4766782443@sina.cn");
    addressPasswordMap.put("a476678244@sina.cn", "sf1234");
    addressPasswordMap.put("a4766782441@sina.cn", "sf1234");
    addressPasswordMap.put("a4766782442@sina.cn", "sf1234");
    addressPasswordMap.put("a4766782443@sina.cn", "sf1234");
    addresses.addAll(template);
  }

  public static String getAddress() {
    int scope = addresses.size();
    int randomIndex = new Random().nextInt(scope);
    return addresses.get(randomIndex);
  }

  public static String getPassword(String address) {
    return addressPasswordMap.get(address);
  }

  public static List<String> getEmailAddresses() {
    return addresses;
  }
}
