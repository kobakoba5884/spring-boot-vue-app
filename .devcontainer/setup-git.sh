#!/bin/bash

git config --global --add safe.directory "${HOME}/workspace"

git config --global init.defaultBranch main

git config --global core.hooksPath "${HOME}/workspace/.githooks"

chmod +x ../.githooks/pre-commit