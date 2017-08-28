#!/bin/bash
set -e

pastebinit -i ./app/build/reports/lint-results.html -f html -a lint-results-${TRAVIS_BUILD_NUMBER}-html
pastebinit -i ./app/build/reports/checkstyle/checkstyle.html -f html -a checkstyle-results-${TRAVIS_BUILD_NUMBER}-html
pastebinit -i ./app/debug/logcat/logcat -a logcat-${TRAVIS_BUILD_NUMBER}

