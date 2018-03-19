package com.adidas.mashery.plugins.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.Collectors;

import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressString;
import inet.ipaddr.AddressStringException;

public class IpRestriction {

  private List<IPAddress> whitelist;
  private List<IPAddress> blacklist;

  public IpRestriction(String rawWhitelist, String rawBlacklist) {
    whitelist = IpRestriction.parseIpAddresses(rawWhitelist);
    blacklist = IpRestriction.parseIpAddresses(rawBlacklist);
  }

  public static List<IPAddress> parseIpAddresses(String rawAddresses) {
    if(rawAddresses == null) {
      return null;
    }

    return Arrays.stream(rawAddresses.split(","))
      .map(rawIp -> rawIp.trim())
      .distinct()
      .map(rawIp -> {
        try {
          return new IPAddressString(rawIp).toAddress();
        } catch (AddressStringException e) {
          System.out.println("ERROR: Can't parse ip address: " + rawIp + " Reason: " + e.getMessage());
          return null;
        }
      })
      .collect(Collectors.toList());
  }

  private static Boolean contains(List<IPAddress> list, IPAddress ip) {
    Optional<IPAddress> address = list.stream()
      .filter(listIp -> listIp.contains(ip))
      .findFirst();

    return address.isPresent();
  }

  public Boolean isAllowed(String rawIp) {
    IPAddress ip;

    try {
      ip = new IPAddressString(rawIp).toAddress();
    } catch (AddressStringException e) {
      System.out.println("ERROR: Can't parse ip address: " + rawIp + " Reason: " + e.getMessage());
      return false;
    }

    if(blacklist != null && IpRestriction.contains(blacklist, ip)) {
      return false;
    }

    if(whitelist != null && !IpRestriction.contains(whitelist, ip)) {
      return false;
    }

    return true;
  }
}
