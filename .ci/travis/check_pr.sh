#!/bin/bash

# Exit for failures
# Attention, there is no "-x" to avoid problems on Travis
set -e

#If not a PR then exit with status 0
if [[ ! $TRAVIS_PULL_REQUEST =~ ^([0-9]*)$ ]]; then exit 0; fi

#Project variables
ORGANISATION_NAME=MobileApplicationsClub
PROJECT_NAME=ARD

#URL of PR commits
LINK_COMMITS=https://api.github.com/repos/$ORGANISATION_NAME/$PROJECT_NAME/pulls/$TRAVIS_PULL_REQUEST/commits
#Verify link not null
if [ -z "$LINK_COMMITS" ]; then
    echo "Link to commits is invalid, $LINK_COMMITS. Exiting...";
    exit 1;
else
    echo 'Commits link is '$LINK_COMMITS;
fi

#Get all commits
COMMITS=$(curl -s $LINK_COMMITS | jq '.[0] | .commit.message')
#Verify commits not null
if [ -z "$COMMITS" ]; then
    echo 'Commits are null. Exiting...'
    exit 2;
else
    echo 'Commit messages from github: '
    echo $COMMITS
fi

#Extract Issue number from commits
ISSUE_NUMBER=$(echo $COMMITS | sed -e 's/^.*Issue //' | sed -e 's/:.*//')
RESULT=0
if [[ $ISSUE_NUMBER =~ ^#[0-9]+$ ]]; then
    echo 'Issue number: '$ISSUE_NUMBER && RESULT=0
else
    echo $ISSUE_NUMBER
    echo 'Could not get Issue number or format is incorrect. Exiting...'
    exit 3;
fi

LINK_PR=https://api.github.com/repos/$ORGANISATION_NAME/$PROJECT_NAME/pulls/$TRAVIS_PULL_REQUEST
LINK_ISSUE=https://api.github.com/repos/$ORGANISATION_NAME/$PROJECT_NAME/issues/${ISSUE_NUMBER:1}
REGEXP=($ISSUE_NUMBER\|https://github.com/$ORGANISATION_NAME/$PROJECT_NAME/issues/${ISSUE_NUMBER:1})
PR_DESC=$(curl -s $LINK_PR | jq '.body' | grep -E $REGEXP | cat )
echo 'PR Description grepped:'${PR_DESC:0:80}
if [[ -z $PR_DESC ]]; then
     echo 'Please put a reference to an Issue in the PR description, this will bind the Issue to your PR in Github' && RESULT=1;
fi
LABEL_APRV=$(curl -s $LINK_ISSUE | jq '.labels [] | .name' | grep approved | cat | wc -l )
if [[ $LABEL_APRV == 0 ]]; then
     echo 'You are providing a PR for an Issue that is not approved yet, please ask admins to approve your Issue first' && RESULT=1;
fi

if [[ $RESULT == 0 ]]; then
      echo 'PR validation succeeded.';
else
      echo 'PR validation failed.' && false;
fi
