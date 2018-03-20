# Mashery Plugins (Processors)

Collection of classes which can be used as Processors in Mashery

## Installation

1. Install [maven](https://maven.apache.org/install.html)
2. Install mashery sdk
    1. Go to `./sdk` folder (`cd ./sdk`)
    2. Run following commands to install mashery sdk to local maven repository
    3. `mvn install:install-file -Dfile=com.mashery.http_1.0.0.v20130130-0044.jar -DgroupId=com.mashery -DartifactId=http -Dversion=1.0.0.v20130130-0044 -Dpackaging=jar -DgeneratePom=true`
    4. `mvn install:install-file -Dfile=com.mashery.trafficmanager.sdk_1.2.0.v20170407-0520.jar -DgroupId=com.mashery -DartifactId=trafficmanager -Dversion=1.2.0.v20170407-0520 -Dpackaging=jar -DgeneratePom=true`
    5. `mvn install:install-file -Dfile=com.mashery.util_1.0.0.v20130214-0015.jar -DgroupId=com.mashery -DartifactId=util -Dversion=1.0.0.v20130214-0015 -Dpackaging=jar -DgeneratePom=true`
3. Return to root of this folder `cd ..`
4. Run `mvn install`


## How to

- Documentation for mashery SDK is in `./sdk/javadoc` (on OSX run `open ./sdk/javadoc/index.html`)


## Preprocessors

### IP Restriction

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

## Tests

- Run `mvn test` (`mvn clean test` when some cached issues)
