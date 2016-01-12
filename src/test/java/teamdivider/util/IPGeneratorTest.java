/*
 * $Id$
 */
package teamdivider.util;

import java.net.Inet4Address;
import java.net.UnknownHostException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class IPGeneratorTest {

  public static void main(String[] args) {

  }

  @Test
  public void test() throws UnknownHostException {
    String ipAddress = Inet4Address.getLocalHost().getHostAddress();
    System.out.println("-----------");
    System.out.println("IP Address:" + ipAddress);
    System.out.println("-----------");
  }
  
  @Configuration
  @ComponentScan("teamdivider.*")
  public static class TestConfiguration{}
}
