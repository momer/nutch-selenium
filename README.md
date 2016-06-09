Nutch Selenium
==============

This plugin allows you to fetch javascript pages using Selenium, while relying on the rest of the awesome Nutch stack! This allows you to

A) Leverage Nutch, a world class web crawler

B) Not have to use some paid service just to perform large-scale javascript/ajax aware web crawls

C) Not have to wait another 2 years for Nutch to patch in either the [Ajax crawler hashbang workaround](https://issues.apache.org/jira/browse/NUTCH-1323) and then, not having to patch it to get the use case of ammending the original url with the hashbang-workaround's content.

The underlying code is based on the nutch-htmlunit plugin, which was in turn based on nutch-httpclient. I also have patches to send through on nutch-htmlunit which get it working with nutch 2.2.1, so stay tuned if you want to use htmlunit for some reason.


## IMPORTANT NOTES:

~This plugin is currently being merged into the Nutch Core - see [issue #1933 on Nutch's JIRA](https://issues.apache.org/jira/browse/NUTCH-1933)~ 

1. This plugin is currently in the nutch core. See [lib-selenium](https://github.com/apache/nutch/tree/master/src/plugin/lib-selenium) and [protocol-selenium](https://github.com/apache/nutch/tree/master/src/plugin/protocol-selenium).

2. As a result of #1, this plugin is unsupported on github. Please see the [Nutch JIRA](https://issues.apache.org/jira/browse/NUTCH/?selectedTab=com.atlassian.jira.jira-projects-plugin:summary-panel) for issues. 

## Installation (tested on Ubuntu 14.0x)

Part 1: Setting up Selenium

A) Ensure that you have Firefox installed. Selenium 2.42.2 will not work with latest Firefox, so I recommend to use Firefox 29.
```
# More info about the package @ [launchpad](https://launchpad.net/ubuntu/trusty/+source/firefox)

wget https://ftp.mozilla.org/pub/firefox/releases/29.0/linux-x86_64/en-US/firefox-29.0.tar.bz2
tar -xjf firefox-29.0.tar.bz2
sudo mv firefox /opt/
sudo ln -s /opt/firefox/firefox /usr/bin/firefox
```
B) Install Xvfb and its associates
```
sudo apt-get install xorg synaptic xvfb gtk2-engines-pixbuf xfonts-cyrillic xfonts-100dpi \
    xfonts-75dpi xfonts-base xfonts-scalable freeglut3-dev dbus-x11 openbox x11-xserver-utils \
    libxrender1 cabextract
```
C) Set a display for Xvfb, so that firefox believes a display is connected
```
sudo /usr/bin/Xvfb :11 -screen 0 1024x768x24 &
sudo export DISPLAY=:11
```
Part 2: Installing plugin for Nutch (where NUTCH_HOME is the root of your nutch install)

A) Add Selenium to your Nutch dependencies
```
<!-- NUTCH_HOME/ivy/ivy.xml -->

<ivy-module version="1.0">
  <dependencies>
    ...
    <!-- begin selenium dependencies -->
    <dependency org="org.seleniumhq.selenium" name="selenium-java" rev="2.42.2" />

    <dependency org="com.opera" name="operadriver" rev="1.5">
      <exclude org="org.seleniumhq.selenium" name="selenium-remote-driver" />
    </dependency>
    <!-- end selenium dependencies -->
  </dependencies>
</ivy-module>
```
B) Add the required plugins to your `NUTCH_HOME/src/plugin/build.xml`
```
<!-- NUTCH_HOME/src/plugin/build.xml -->

<project name="Nutch" default="deploy-core" basedir=".">
  <!-- ====================================================== -->
  <!-- Build & deploy all the plugin jars.                    -->
  <!-- ====================================================== -->
  <target name="deploy">
    ... 
    <ant dir="lib-selenium" target="deploy"/>
    <ant dir="protocol-selenium" target="deploy" />
  </target>
      ...
</project>
```
C) Ensure that the plugin will be used as the fetcher/initial parser in your config
```
<!-- NUTCH_HOME/conf/nutch-site.xml -->

<configuration>
  ...
  <property>
    <name>plugin.includes</name>
    <value>protocol-selenium|urlfilter-regex|index-(basic|more)|query-(basic|site|url|lang)|indexer-elastic|nutch-extensionpoints|parse-(text|html|msexcel|msword|mspowerpoint|pdf)|summary-basic|scoring-opic|urlnormalizer-(pass|regex|basic)|parse-(html|tika|metatags)|index-(basic|anchor|more|metadata)</value>
  </property>
```
D) Add the required configuration to your `NUTCH_HOME/runtime/local/conf/nutch-site.xml`
```
  <property>
    <name>urlselenium.regex.file</name>
    <value>regex-urlselenium.txt</value>
    <description>Name of file on CLASSPATH containing regular expressions
    used by protocol-selenium plugin.</description>
  </property>
```
E) Create regex-urlselenium.txt file in NUTCH_HOME/runtime/local/conf and register the URLs that will be going to use selenium. The URLs regex format should be the same as what you already use in regex-urlfilter.txt file.

F) Add the plugin folders to your installation's `NUTCH_HOME/src/plugin` directory

![Nutch plugin directory](http://i.imgur.com/CzLqoqO.png)

G) Compile nutch
```
ant runtime
```

H) Start your web crawl (Ensure that you followed the above steps and have started your xvfb display as shown above)
```
NUTCH_HOME/runtime/local/bin/crawl /opt/apache-nutch-2.2.1/urls/ webpage $NUTCH_SOLR_SERVER $NUTCH_CRAWL_DEPTH
```

