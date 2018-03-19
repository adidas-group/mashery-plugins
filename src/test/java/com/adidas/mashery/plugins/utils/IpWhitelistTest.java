import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Map;
import java.util.HashMap;

import com.adidas.mashery.plugins.utils.IpWhitelist;

public class IpWhitelistTest {
  @Test
  public void allowIp() {
    Map<String,String> whitelist = new HashMap<String, String>();
    whitelist.put("name", "192.169.1.1");

    Boolean result = IpWhitelist.allowed(whitelist, "192.168.1.1");

    assertTrue(result);
  }

  @Test
  public void disallowIp() {
    Map<String,String> whitelist = new HashMap<String, String>();
    whitelist.put("name", "192.169.1.2");

    Boolean result = IpWhitelist.allowed(whitelist, "192.168.1.2");

    assertTrue(!result);
  }

  // TODO: expanding of ip like 192.168.1/*
}
