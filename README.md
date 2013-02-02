# Welcome to PAX-WICKET

PAX-WICKET is a small framework which helps to integrate [Apache Wicket](http://wicket.apache.org)
into the [OSGi](http://www.osgi.org) component framework.

[![Build Status](https://travis-ci.org/ops4j/org.ops4j.pax.wicket.png?branch=master)](https://travis-ci.org/ops4j/org.ops4j.pax.wicket)

## Introduction

PAX-WICKET provides the following features:

* Full integration at HttpService Level
* Bean and Service injection on bundle level from Spring and Blueprint
* A delegating classloader and injection model.

For a full list of features and a more detailed documentation see the
[PAX-WICKET wiki](http://ops4j1.jira.com/wiki/display/paxwicket/Pax+Wicket).

## Get in contact

Since the code is always moving faster than the documentation it is well possible that your use-case is
possible with PAX-WICKET although not documented by now. Feel free to jump on to our [mailing-lists](http://ops4j1.jira.com/wiki/display/ops4j/listinfo)
or [IRC channels](https://ops4j1.jira.com/wiki/display/ops4j/ircinfo) and ask your questions there.

## Quickstart

The easiest way to get in contact with PaxWicket is to read the quickstart documentation at the
[PAX-WICKET wiki](http://ops4j1.jira.com/wiki/display/paxwicket/Pax+Wicket).

If you prefer to read code instead of manuals there are various samples available.

* mvn clean install
* cd samples
* mvn pax:provision
* point the browser to [http://localhost:8080/navigation/](http://localhost:8080/navigation/). From here on you'll find links and (short) explanations to the available examples.
* If you're not firm with the gogo command line the following commands might help:
    * stop 0 (kills the samples)
    * bundles (shows all bundles)
    * bundle BUNDLEID (shows more details to a bundle)
    * stop BUNDLEID (stops a bundle)
    * start BUNDLEID (starts a bundle again)
    * help (for a full list of possible commands)
    * help COMMANDNAME (full description of a command)

## Build PAX-WICKET

PAX-WICKET uses [Apache Maven](http://maven.apache.org) as its build system. Simply checkout the sources and run
"mvn clean install". This will build PAX-WICKET, the samples and run all integration tests.

## Code on PAX-WICKET

PAX-WICKET is developed using Intellij and Eclipse. Either use the plugins in the IDEs or simply run "mvn idea:idea"
or "mvn eclipse:eclipse" before and import them into your IDE.

