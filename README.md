# ARD Android Companion
[![Build Status](https://travis-ci.org/MobileApplicationsClub/ARD.svg?branch=master)](https://travis-ci.org/MobileApplicationsClub/ARD)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/1fc788215a9b42b3bb6354ae0a1ac7ce)](https://www.codacy.com/app/kukreja-vikramaditya/ARD?utm_source=github.com&utm_medium=referral&utm_content=MobileApplicationsClub/ARD&utm_campaign=badger)
[![codebeat badge](https://codebeat.co/badges/0ecdbf2c-7c80-48cf-b66e-a3e4c1090546)](https://codebeat.co/projects/github-com-mobileapplicationsclub-ard-master)
[![codecov](https://codecov.io/gh/MobileApplicationsClub/ARD/branch/master/graph/badge.svg?token=aoOIeczRVC)](https://codecov.io/gh/MobileApplicationsClub/ARD)
[![Dependency Status](https://www.versioneye.com/user/projects/5949512e6725bd0063d1dac5/badge.svg?style=flat-square)](https://www.versioneye.com/user/projects/5949512e6725bd0063d1dac5)
[![GitHub pull requests](https://img.shields.io/github/issues-pr/MobileApplicationsClub/ARD.svg)](https://github.com/MobileApplicationsClub/ARD/pulls)
[![label](https://img.shields.io/github/issues-raw/MobileApplicationsClub/ARD/approved.svg)](https://github.com/MobileApplicationsClub/ARD/issues?q=is%3Aopen+is%3Aissue+label%3Aapproved)

[![GitHub contributors](https://img.shields.io/github/contributors/MobileApplicationsClub/ARD.svg)](https://github.com/MobileApplicationsClub/ARD/graphs/contributors)
[![Slack](https://img.shields.io/badge/Slack-join%20chat-brightgreen.svg)](https://join.slack.com/macbitsgoa/shared_invite/MjAxNDMzNzM0NjEwLTE0OTgwNjc4NDAtMmVmMWQyZjk2MA)

This app runs on Android OS version above and including Lollipop (*API 21*).

## Project uses the following CIs
[Travis CI](https://www.travis-ci.org) for testing. Travis CI reports can be found [here](https://travis-ci.org/MobileApplicationsClub/ARD).

[Codecov](https://codecov.io/gh) for coverage. Reports [here](https://codecov.io/gh/MobileApplicationsClub/ARD).

[VersionEye](https://www.versioneye.com/user/projects/5949512e6725bd0063d1dac5?child=summary) to keep track of dependencies.


 Project follows the naming convention mentioned [here](http://jeroenmols.com/blog/2016/03/07/resourcenaming/) and [here.](https://github.com/ribot/android-guidelines/blob/master/project_and_code_guidelines.md) Before starting contributions please make sure you are thorough with this convention. If you have any suggestions that you would like to share or want to report a bug, use the **Issues** tab.

For any issues faced during installations, head over to [Slack.](https://macbitsgoa.slack.com)

Project hosted under [Mobile Applications Club](https://github.com/MobileApplicationsClub), BITS Goa

## Project Setup and Build Instructions

### Requirements

> - Android Studio *2.3* or above
> - Android SDK *Tools 26.0.0* or above
> - Android SDK *Platform-tools 26.0.2* or above
> - Android SDK *Build-tools 26.0.0* or above
> - *Extras* (in SDK Manager)

### Installation

1. **Fork** this repository to your account on GitHub

2. Clone to your local storage using *git*. Open a terminal and paste

```bash
git clone https://github.com/yourusername/ARD.git
```

3. Open Android Studio and click ***Open*** > Navigate to ***ARD***  folder > Press ***Select***

4. In android studio console execute

```bash
git remote add upstream https://github.com/MobileApplicationsClub/ARD
```

### Contributing
See `.github/CONTRIBUTING.md`.

If you're not proficient with **git**, it'll be better if you follow `.github/git-workflow.md`.

### Build and Test

While normal (manual) testing, you can run the app using `app` configuration in Android Studio.

However, we consider code coverage data to assess PR. Which means you will have to write tests that cover as much portion of your patch as possible. To evaluate the tests and see the coverage stats locally, do following:

 ```bash
 ./gradlew clean build coverage
 ```

 If all goes well, reports will be generated at `app/build/reports` directory.
