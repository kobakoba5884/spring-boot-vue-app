#!/bin/bash

git config --global --add safe.directory "${HOME}/workspace"

git config --global init.defaultBranch main

git config --global user.name "${GIT_USER}"

git config --global user.email "${GIT_EMAIL}"

git config --global core.editor "code --wait"

git config core.hooksPath "${HOME}/workspace/.githooks"

chmod +x ../.githooks/pre-commit

