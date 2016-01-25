/*
 * $Id$
 */
package teamdivider.util;

import java.net.Inet4Address;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class IPGeneratorTest {

  private static final Logger log = Logger.getLogger(IPGeneratorTest.class);
  
  public static void main(String[] args) {

  }

  @Test
  public void test() throws UnknownHostException {
    String ipAddress = Inet4Address.getLocalHost().getHostAddress();
    log.info("-----------");
    log.info("IP Address:" + ipAddress);
    log.info("-----------");
  }
  
  @Configuration
  @ComponentScan("teamdivider.*")
  public static class TestConfiguration{}
}
