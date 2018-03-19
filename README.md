# Preprocessors

## IP Restriction

- *name*: `com.adidas.mashery.plugins.IpRestrictionProcessor`
- *description*: Allow or restrict access to particular endpoint based on list of IP addresses
- *parameters*:
  - `whitelist`: list of allowed IP addresses separated by colon.
  - `blacklist`: list of restricted IP addresses separated by colon.
  - IP Address can be:
    1. simple `192.168.1.1`
    2. subnet `192.168.2.0/24`
    3. wildcard `192.168.3.*`
  - example:

```
whitelist: 192.168.1.1,192.168.2.0/24,192.168.3.*
blacklist: 192.168.1.3,192.168.4.0/24,192.168.5.*
```
