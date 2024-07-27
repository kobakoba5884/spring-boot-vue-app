#!/bin/bash

# Check if required environment variables are set
if [ -z "$GIT_USER" ] || [ -z "$GIT_EMAIL" ]; then
    echo "GIT_USER or GIT_EMAIL is not set."
    exit 1
fi

# Store the workspace path in a variable
WORKSPACE_DIR="${HOME}/workspace"

# Apply Git configurations
echo "Setting up git configurations..."

git config --global --add safe.directory "${WORKSPACE_DIR}" || { echo "Failed to add safe directory"; exit 1; }
git config --global init.defaultBranch main || { echo "Failed to set default branch"; exit 1; }
git config --global user.name "${GIT_USER}" || { echo "Failed to set user name"; exit 1; }
git config --global user.email "${GIT_EMAIL}" || { echo "Failed to set user email"; exit 1; }
git config --global core.editor "code --wait" || { echo "Failed to set core editor"; exit 1; }
git config --global pull.rebase true

# Change to the workspace directory
cd "${WORKSPACE_DIR}" || { echo "Failed to change directory to ${WORKSPACE_DIR}"; exit 1; }

# Set commit template and hooks path
git config --global commit.template "${WORKSPACE_DIR}/.devcontainer/git/commitTemplate.txt" || { echo "Failed to set commit template"; exit 1; }
git config core.hooksPath "${WORKSPACE_DIR}/.githooks" || { echo "Failed to set hooks path"; exit 1; }

# Make the pre-commit hook executable
chmod +x "${WORKSPACE_DIR}/.githooks/pre-commit" || { echo "Failed to make pre-commit hook executable"; exit 1; }

# Set git alias
git config --global alias.co 'commit'
git config --global alias.cm 'commit -m'
git config --global alias.st 'status'
git config --global alias.br 'branch'
git config --global alias.ch 'checkout'
git config --global alias.cb 'checkout -b'
git config --global alias.p 'push'
git config --global alias.rv 'remote -v'
git config --global alias.d 'diff'
git config --global alias.gl 'config --global -l'
git config --global alias.cleanup-merged '!git branch --merged | grep -vE "^\*|^\s*main$|^\s*master$" | xargs -n 1 git branch -d'

echo "Git configuration completed successfully."

# Setup keycloak
SETUP_KC_DIR="${HOME}/workspace/.devcontainer/docker/keycloak"

chmod +x $SETUP_KC_DIR/setup-keycloak.sh

source $SETUP_KC_DIR/setup-keycloak.sh


