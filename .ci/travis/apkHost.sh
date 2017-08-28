#!/bin/bash
set -e

curl -i -X POST $SERVER_POST -H "Content-Type: application/vnd.android.package-archive" -H "Project-Name: ARD" -H "Sub-Directory: $TRAVIS_BRANCH" -H "Extra-Data: $TRAVIS_EVENT_TYPE--PR-$TRAVIS_PULL_REQUEST--Travis-Build-$TRAVIS_BUILD_NUMBER" --data-binary "@app/build/outputs/apk/app-debug.apk" -v

