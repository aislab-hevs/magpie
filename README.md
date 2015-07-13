# [MAGPIE](http://www.hevs.ch/en/mini-sites/projets-produits/aislab/projets/magpie-4826)
[![Build Status](https://travis-ci.org/aislab-hevs/magpie.svg)](https://travis-ci.org/aislab-hevs/magpie)

## Description
MAGPIE agent platform for Android

## Table of contents
- [Required dependencies](#required-dependencies)
- [Quick start](#quick-start)
- [How to import the project in Android Studio](#how-to-import-the-project-in-android-studio)
- [Run the tests](#run-the-tests)
- [Documentation](#documentation)
- [Copyright and license](#copyright-and-license)
 
## Required dependencies
- [Andorid Studio & Android SDK](https://developer.android.com/sdk/installing/index.html?pkg=studio)
- [Gradle](http://www.gradle.org/installation)

The Android Build Tools version used is 21.1.2. It is necessary to install this version or change the version to the installed version in /app/build.gradle/ buildToolsVersion "21.1.2"

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
