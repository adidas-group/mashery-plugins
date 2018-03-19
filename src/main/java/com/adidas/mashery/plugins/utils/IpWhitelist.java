package com.adidas.mashery.plugins.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.Collectors;

public class IpWhitelist {
  private Map<String, String> params;

  public static Boolean allowed(Map<String, String> params, String ip) {
    // List<String> whitelist = IpWhitelist.ipWhitelistParamsToList(params);

    return true;
  }

  // public IpWhitelist(Map<String, String> ipParams) {
  //   params = ipParams
  // }
  //
  // public Boolean allowed(String ip) {
  //   return true;
  // }

  public static List<String> ipWhitelistParamsToList(Map<String, String> params) {
    return params
      .values() // Collection<String>
      .stream() // Stream<String>
      .flatMap(ips -> Arrays.stream(ips.split(",")))
      .map(ip -> ip.trim())
      .distinct()
      .collect(Collectors.toList());
  }
}
