#!/bin/bash

# Setup git
SETUP_GIT_SH="${HOME}/workspace/.devcontainer/docker/workspace/git/setup-git.sh"

chmod +x $SETUP_GIT_SH

source $SETUP_GIT_SH

# Setup keycloak
SETUP_KC_SH="${HOME}/workspace/.devcontainer/docker/keycloak/setup-keycloak.sh"

chmod +x $SETUP_KC_SH

source $SETUP_KC_SH
