#!/bin/bash

KEYCLOAK_HOST=keycloak
KEYCLOAK_URL=http://$KEYCLOAK_HOST:$KEYCLOAK_HTTP_PORT
KEYCLOAK_CLIENT_ID=admin-cli
KEYCLOAK_REALM_NAME=master
USER_NAME="user-api"
USER_EMAIL="user-api@example.com"
USER_FIRST_NAME="user"
USER_LAST_NAME="api"
USER_PASSWORD="password"

# Function to wait for Keycloak to start
wait_for_keycloak() {
    echo "Waiting for Keycloak to start... ($KEYCLOAK_URL)"
    until curl -s ${KEYCLOAK_URL}; do
        sleep 5
    done
}

# Function to get access token
get_access_token() {
    echo "Logging into Keycloak as admin..."
    ACCESS_TOKEN=$(curl -d "client_id=$KEYCLOAK_CLIENT_ID" -d "username=$KEYCLOAK_ADMIN" -d "password=$KEYCLOAK_ADMIN_PASSWORD" -d "grant_type=password" "$KEYCLOAK_URL/realms/$KEYCLOAK_REALM_NAME/protocol/openid-connect/token" | jq -r '.access_token')
    if [ -z "$ACCESS_TOKEN" ] || [ "$ACCESS_TOKEN" == "null" ]; then
        echo "Failed to get access token"
        exit 1
    fi
    echo "Logged in successfully."
}

# Function to check if realm exists and delete it
delete_realm_if_exists() {
    REALM_EXISTS=$(curl -s -H "Authorization: Bearer $ACCESS_TOKEN" "$KEYCLOAK_URL/admin/realms" | jq -r --arg REALM_NAME "$REALM_NAME" '.[] | select(.realm == $REALM_NAME) | .realm')
    if [ "$REALM_EXISTS" == "$REALM_NAME" ]; then
        echo "Realm $REALM_NAME exists. Deleting realm..."
        curl -s -X DELETE "$KEYCLOAK_URL/admin/realms/$REALM_NAME" \
            -H "Authorization: Bearer $ACCESS_TOKEN"
        echo "Realm $REALM_NAME deleted."
    else
        echo "Realm $REALM_NAME does not exist."
    fi
}

# Function to create realm
create_realm() {
    curl -s -X POST -H "Authorization: Bearer $ACCESS_TOKEN" -H "Content-Type: application/json" "$KEYCLOAK_URL/admin/realms" -d "$(jq -n --arg realm "$REALM_NAME" '{realm: $realm, enabled: true}')"
    echo "Realm created: $REALM_NAME"
}

# Function to create client
create_client() {
    local clientId=$1
    local clientConfig=$2
    curl -s -X POST "$KEYCLOAK_URL/admin/realms/$REALM_NAME/clients" \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $ACCESS_TOKEN" \
        -d "$clientConfig"
    echo "Client created: $clientId"
}

# Function to get client UUID
get_client_uuid() {
    local clientId=$1
    curl -s -X GET "$KEYCLOAK_URL/admin/realms/$REALM_NAME/clients?clientId=$clientId" \
        -H "Authorization: Bearer $ACCESS_TOKEN" | jq -r '.[0].id'
}

# Function to set client secret
set_client_secret() {
    local clientUuid=$1
    local bashrcFile=$2
    local secretVarName=$3

    CLIENT_SECRET=$(curl -s "$KEYCLOAK_URL/admin/realms/$REALM_NAME/clients/$clientUuid/client-secret" \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $ACCESS_TOKEN" | jq -r '.value')

    sed -i "/export $secretVarName=/d" $bashrcFile
    echo "export $secretVarName=\"${CLIENT_SECRET}\"" >> $bashrcFile
}

# Function to create user
create_user() {
    local username=$1
    local email=$2
    local firstName=$3
    local lastName=$4
    local password=$5

    curl -s -X POST "$KEYCLOAK_URL/admin/realms/$REALM_NAME/users" \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $ACCESS_TOKEN" \
        -d "$(jq -n --arg username "$username" --arg email "$email" \
            --arg firstName "$firstName" --arg lastName "$lastName" \
            '{username: $username, email: $email, firstName: $firstName, lastName: $lastName, enabled: true}')"
    echo "User created: $username"

    USER_ID=$(curl -s -X GET "$KEYCLOAK_URL/admin/realms/$REALM_NAME/users?username=$username" \
        -H "Authorization: Bearer $ACCESS_TOKEN" | jq -r '.[0].id')

    curl -s -X PUT "$KEYCLOAK_URL/admin/realms/$REALM_NAME/users/$USER_ID/reset-password" \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $ACCESS_TOKEN" \
        -d "$(jq -n --arg password "$password" \
            '{type: "password", temporary: false, value: $password}')"
    echo "Password set for user: $username"
}

# Main script execution
wait_for_keycloak
get_access_token
delete_realm_if_exists
create_realm

# Creating clients
create_client "$BACKEND_CLIENT_ID" "$(jq -n --arg clientId "$BACKEND_CLIENT_ID" '{clientId: $clientId, enabled: true, publicClient: false, standardFlowEnabled: false, directAccessGrantsEnabled: false}')"
create_client "$FRONTEND_CLIENT_ID" "$(jq -n --arg clientId "$FRONTEND_CLIENT_ID" --argjson redirect_uris '["'"$REDIRECT_URIS"'"]' '{clientId: $clientId, enabled: true, redirectUris: $redirect_uris, publicClient: false, consentRequired: true, directAccessGrantsEnabled: false}')"


# Retrieving and setting client secrets
BASHRC_FILE="${HOME}/.bashrc"
BACKEND_CLIENT_UUID=$(get_client_uuid "$BACKEND_CLIENT_ID")
FRONTEND_CLIENT_UUID=$(get_client_uuid "$FRONTEND_CLIENT_ID")

set_client_secret "$BACKEND_CLIENT_UUID" "$BASHRC_FILE" "BACKEND_CLIENT_SECRET"
set_client_secret "$FRONTEND_CLIENT_UUID" "$BASHRC_FILE" "FRONTEND_CLIENT_SECRET"

# Creating user
create_user "$USER_NAME" "$USER_EMAIL" "$USER_FIRST_NAME" "$USER_LAST_NAME" "$USER_PASSWORD"

echo "Keycloak configuration completed successfully."





