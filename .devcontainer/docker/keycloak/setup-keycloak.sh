#!/bin/bash

KC_BIN_DIR="${HOME}/.keycloak/keycloak-15.0.2/bin"
REALM_NAME=demo-api
USER_NAME=user-api
BACKEND_CLIENT_ID=demo-resourceserver
FRONTEND_CLIENT_ID=demo-client
BACKEND_CLIENT_SECRET=$(uuidgen)
FRONTEND_CLIENT_SECRET=$(uuidgen)
REDIRECT_URIS=http://localhost:8081/gettoken

BASHRC_FILE="${HOME}/.bashrc"

sed -i '/export KC_BIN_DIR=/d' $BASHRC_FILE
sed -i '/export REALM_NAME=/d' $BASHRC_FILE
sed -i '/export USER_NAME=/d' $BASHRC_FILE
sed -i '/export BACKEND_CLIENT_ID=/d' $BASHRC_FILE
sed -i '/export FRONTEND_CLIENT_ID=/d' $BASHRC_FILE
sed -i '/export BACKEND_CLIENT_SECRET=/d' $BASHRC_FILE
sed -i '/export FRONTEND_CLIENT_SECRET=/d' $BASHRC_FILE
sed -i '/export REDIRECT_URIS=/d' $BASHRC_FILE

echo "export KC_BIN_DIR=\"${KC_BIN_DIR}\"" >> $BASHRC_FILE
echo "export REALM_NAME=\"${REALM_NAME}\"" >> $BASHRC_FILE
echo "export USER_NAME=\"${USER_NAME}\"" >> $BASHRC_FILE
echo "export BACKEND_CLIENT_ID=\"${BACKEND_CLIENT_ID}\"" >> $BASHRC_FILE
echo "export FRONTEND_CLIENT_ID=\"${FRONTEND_CLIENT_ID}\"" >> $BASHRC_FILE
echo "export BACKEND_CLIENT_SECRET=\"${BACKEND_CLIENT_SECRET}\"" >> $BASHRC_FILE
echo "export FRONTEND_CLIENT_SECRET=\"${FRONTEND_CLIENT_SECRET}\"" >> $BASHRC_FILE
echo "export REDIRECT_URIS=\"${REDIRECT_URIS}\"" >> $BASHRC_FILE

source $BASHRC_FILE

cd "${KC_BIN_DIR}"

./standalone.sh &

echo "Waiting for Keycloak to start..."

until curl -s http://localhost:8080/auth; do
    sleep 5
done

# Logging into Keycloak as admin...
./kcadm.sh config credentials --server http://localhost:8080/auth --realm master --user "${KEYCLOAK_ADMIN}" --password "${KEYCLOAK_ADMIN_PASSWORD}"
echo "Logged in successfully."

REALM_EXISTS=$(./kcadm.sh get realms | jq -r --arg REALM_NAME "$REALM_NAME" '.[] | select(.realm == $REALM_NAME) | .realm')

if [ "$REALM_EXISTS" == "$REALM_NAME" ]; then
    echo "Realm $REALM_NAME exists. Deleting realm..."
    ./kcadm.sh delete realms/$REALM_NAME
    echo "Realm $REALM_NAME deleted."
else
    echo "Realm $REALM_NAME does not exist."
fi

# Creating realm
./kcadm.sh create realms -s realm=$REALM_NAME -s enabled=true
echo "Realm created: $REALM_NAME"

# Creating client
./kcadm.sh create clients -r $REALM_NAME -s clientId=$BACKEND_CLIENT_ID -s enabled=true -s publicClient=false -s standardFlowEnabled=false -s directAccessGrantsEnabled=false
./kcadm.sh create clients -r $REALM_NAME -s clientId=$FRONTEND_CLIENT_ID -s enabled=true -s publicClient=false -s 'redirectUris=["'"$REDIRECT_URIS"'"]' -s consentRequired=true -s directAccessGrantsEnabled=false

echo "Backend Client created: $BACKEND_CLIENT_ID"
echo "Frontend Client created: $FRONTEND_CLIENT_ID"

# Creating user
EMAIL=user-api@example.com
PASSWORD="password"

./kcadm.sh create users -s username=$USER_NAME -s email=$EMAIL -s enabled=true -s emailVerified=true -r $REALM_NAME
./kcadm.sh set-password -r $REALM_NAME --username $USER_NAME --new-password $PASSWORD -t

echo "User created: $USER_NAME"

# Retrieving client UUID for client
BACKEND_CLIENT_UUID=$(./kcadm.sh get clients -r $REALM_NAME -q clientId=$BACKEND_CLIENT_ID | jq -r '.[] | select(.clientId == "'$BACKEND_CLIENT_ID'") | .id')
FRONTEND_CLIENT_UUID=$(./kcadm.sh get clients -r $REALM_NAME -q clientId=$FRONTEND_CLIENT_ID | jq -r '.[] | select(.clientId == "'$FRONTEND_CLIENT_ID'") | .id')

echo "Backend Client UUID: $BACKEND_CLIENT_UUID"
echo "Frontend Client UUID: $FRONTEND_CLIENT_UUID"

# Updating client secret for client UUID
./kcadm.sh update clients/$BACKEND_CLIENT_UUID -r $REALM_NAME -s "secret=$BACKEND_CLIENT_SECRET"
./kcadm.sh update clients/$FRONTEND_CLIENT_UUID -r $REALM_NAME -s "secret=$FRONTEND_CLIENT_SECRET"

./kcadm.sh get clients/$BACKEND_CLIENT_UUID/client-secret -r $REALM_NAME
./kcadm.sh get clients/$FRONTEND_CLIENT_UUID/client-secret -r $REALM_NAME

echo "Stopping Keycloak server..."

PID=$(lsof -t -i:8080)

kill -9 $PID



