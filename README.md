# [MAGPIE](http://www.hevs.ch/en/mini-sites/projets-produits/aislab/projets/magpie-4826)
[![Build Status](https://travis-ci.org/aislab-hevs/magpie.svg)](https://travis-ci.org/aislab-hevs/magpie)

## Description
MAGPIE agent platform for Android

## Table of contents
- [Contents of MAGPIE] (#contents-of-magpie)
- [Required dependencies](#required-dependencies)
- [Quick start](#quick-start)
- [How to import the project in Android Studio](#how-to-import-the-project-in-android-studio)
- [Run the tests](#run-the-tests)
- [Documentation](#documentation)
- [Copyright and license](#copyright-and-license)

## Contents of MAGPIE
- [library](https://github.com/aislab-hevs/magpie/tree/master/MAGPIE/library): the MAGPIE agent platform.
- [sample-debs](https://github.com/aislab-hevs/magpie/tree/master/MAGPIE/sample-debs): a demo application integrating the use of MAGPIE in a Distributed Event Based System.
- [sample-sensor](https://github.com/aislab-hevs/magpie/tree/master/MAGPIE/sample-sensor): a demo application showing how to use MAGPIE with a BioHarness sensor.
- [server](https://github.com/aislab-hevs/magpie/tree/master/MAGPIE/server): a Spring based server to be used with the sample-debs application. 
 
## Required dependencies
- [Andorid Studio & Android SDK](https://developer.android.com/sdk/installing/index.html?pkg=studio)
- [Gradle](http://www.gradle.org/installation)

The Android Build Tools version used is 23.0.2. It is necessary to install this version or change the version to the installed version in /library/build.gradle/ buildToolsVersion "23.0.2"

## Quick start
Two quick start options are available:

- [Download the latest release](https://github.com/aislab-hevs/magpie/archive/master.zip).
- Clone the reop: `git clone https://github.com/aislab-hevs/magpie.git`.

## How to import the project in Android Studio
- If no project is open in Android Studio:
  - Select "Open an existing Android Studio project"
  - Select the folder /magpie/MAGPIE/
  - Click on "OK"

- If an other project is already open in Android Studio:
  - Select File -> Import Project...
  - Select the folder /magpie/MAGPIE/
  - Click on "OK"

## Run the tests
- In Android Studio
  - Open the "Android" perspective
  - Right click on .ch.hevs.aislab.magpie.simpletest (androidTest) and select "Run" -> "All Tests"
  
  **OR**

  - Open the "Project" perspective
  - Right click on /app/src/androidTest/java/ and select "Run" -> "Run 'All Tests'"

- Command line
  - Switch to /magpie/MAGPIE/
  - Use the command to execute the tests: ./gradlew build connectedCheck

 For testing a running emulator or a connected device is needed.
  
## Copyright and license
Code and documentation copyright 2014-2015 [AISLab HES-SO Valais](http://www.hevs.ch/fr/mini-sites/projets-produits/aislab/). Code released under [the BSD license](https://github.com/aislab-hevs/magpie/blob/master/LICENSE).
