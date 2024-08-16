# devcontainer

## workspace
- package
    - vim
    - curl
    - unzip
    - zip
    - tzdata
    - git
    - atc..
- sdkman
    - java
    - gradle
    - maven
    - kotlin
- nvm
- z

## postgres

## nginx

## keycloak

## docker compose diagram
```mermaid
graph LR
    subgraph docker-network
        subgraph workspace[workspace:9999]
            java
            gradle
            kotlin
            atc...
        end
        postgres[postgres:5432]
        keycloak[keycloak:8080]
    end

    workspace --> postgres
    workspace --> keycloak
```