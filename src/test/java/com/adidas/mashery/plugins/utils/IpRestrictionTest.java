import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Map;
import java.util.HashMap;

import com.adidas.mashery.plugins.utils.IpRestriction;

import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressString;

public class IpRestrictionTest {

  @Test
  public void allowForNoList() {
    IpRestriction restriction = new IpRestriction(null, null);
    assertEquals(true, restriction.isAllowed("192.168.1.1"));
  }

  @Test
  public void whitelistIp() {
    IpRestriction restriction = new IpRestriction("192.168.1.1", null);
    assertEquals(true, restriction.isAllowed("192.168.1.1"));
  }

  @Test
  public void disallowNonWhitelistedIp() {
    IpRestriction restriction = new IpRestriction("192.168.1.1", null);
    assertEquals(false, restriction.isAllowed("192.168.1.2"));
  }

  @Test
  public void whitelistSubnet() {
    IpRestriction restriction = new IpRestriction("192.168.1.0/24", null);
    assertEquals(true, restriction.isAllowed("192.168.1.1"));
    assertEquals(true, restriction.isAllowed("192.168.1.200"));

    assertEquals(false, restriction.isAllowed("192.168.2.1"));
  }

  @Test
  public void blacklistIp() {
    IpRestriction restriction = new IpRestriction(null, "192.168.1.1");
    assertEquals(false, restriction.isAllowed("192.168.1.1"));

    assertEquals(true, restriction.isAllowed("192.168.1.2"));
  }

  @Test
  public void blacklistOverWhitelist() {
    IpRestriction restriction = new IpRestriction("192.168.1.0/24,192.168.2.1", "192.168.1.120");
    assertEquals(true, restriction.isAllowed("192.168.1.100"));
    assertEquals(false, restriction.isAllowed("192.168.1.120"));
  }

  @Test
  public void allowWildcard() {
    IpRestriction restriction = new IpRestriction("192.168.1.*", null);
    assertEquals(true, restriction.isAllowed("192.168.1.1"));
    assertEquals(true, restriction.isAllowed("192.168.1.100"));
  }
}
