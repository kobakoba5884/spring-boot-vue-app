#!/bin/bash

git config --add safe.directory "${HOME}/workspace"

git config init.defaultBranch main

git config core.hooksPath "${HOME}/workspace/.githooks"

chmod +x ../.githooks/pre-commit