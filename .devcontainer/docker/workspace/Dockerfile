# windows 11
FROM ubuntu:22.04

ARG USER_NAME
ARG USER_GROUP_NAME
ARG USER_UID
ARG USER_GID

LABEL maintainer="mizuki"
LABEL version="2.0"
LABEL description="my-workspace"

# general packages
ARG PKG="vim curl unzip zip sudo tzdata git"

SHELL ["/bin/bash", "-c"]

# ---------------------------------- set up ----------------------------------

# setup timezone https://dev.to/grigorkh/fix-tzdata-hangs-during-docker-image-build-4o9m
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo TZ > /etc/timezone

RUN apt-get update \
    && apt-get install -y ${PKG} \
    && groupadd --gid ${USER_GID} ${USER_GROUP_NAME} \
    && useradd --uid ${USER_UID} --shell /bin/bash --gid ${USER_GID} -m ${USER_NAME} \
    && echo %${USER_GROUP_NAME} ALL=\(ALL\) NOPASSWD:ALL > /etc/sudoers.d/${USER_GROUP_NAME} \
    && chmod 0440 /etc/sudoers.d/${USER_GROUP_NAME}

USER ${USER_NAME}

ENV HOME=/home/${USER_NAME}

WORKDIR ${HOME}

# ---------------------------------- sdkman for java ----------------------------------
# https://sdkman.io/install
ARG JAVA_VERSION
ARG GRADLE_VERSION
ARG KOTLIN_VERSION

RUN curl -s "https://get.sdkman.io" | bash \
    && source "${HOME}/.sdkman/bin/sdkman-init.sh" \
    && sdk install java ${JAVA_VERSION} \
    && sdk install gradle ${GRADLE_VERSION} \
    && sdk install kotlin ${KOTLIN_VERSION} \
    && java --version


# ---------------------------------- nvm for node ----------------------------------
# default node version
ARG NODE_VERSION

ARG NODE_GLOBAL_PKG="prettier"

RUN curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.1/install.sh | bash \
    && source "${HOME}/.nvm/nvm.sh" \
    && nvm install ${NODE_VERSION} \
    && npm update -g \
    && npm i --global ${NODE_GLOBAL_PKG}


# ---------------------------------- set up keycloak ----------------------------------
ENV KEYCLOAK_ADMIN=admin
ENV KEYCLOAK_ADMIN_PASSWORD=password
ARG KEYCLOAK_VERSION=25.0.1

RUN curl -L -o keycloak-${KEYCLOAK_VERSION}.tar.gz https://github.com/keycloak/keycloak/releases/download/${KEYCLOAK_VERSION}/keycloak-${KEYCLOAK_VERSION}.tar.gz | bash \
    && mkdir ~/.keycloak \
    && tar xvzf keycloak-${KEYCLOAK_VERSION}.tar.gz \
    && mv keycloak-${KEYCLOAK_VERSION} ~/.keycloak \
    && rm ~/keycloak-${KEYCLOAK_VERSION}.tar.gz \
    && echo "http-port=2424" >> ~/.keycloak/keycloak-${KEYCLOAK_VERSION}/conf/keycloak.conf \
    && echo "alias kc='~/.keycloak/keycloak-${KEYCLOAK_VERSION}/bin/kc.sh'" >> ~/.bashrc

ARG KEYCLOAK_OLD_VERSION=15.0.2

RUN curl -L -o keycloak-${KEYCLOAK_OLD_VERSION}.tar.gz https://github.com/keycloak/keycloak/releases/download/${KEYCLOAK_OLD_VERSION}/keycloak-${KEYCLOAK_OLD_VERSION}.tar.gz | bash \
    && tar xvzf keycloak-${KEYCLOAK_OLD_VERSION}.tar.gz \
    && mv keycloak-${KEYCLOAK_OLD_VERSION} ~/.keycloak \
    && rm ~/keycloak-${KEYCLOAK_OLD_VERSION}.tar.gz \
    && echo "alias kc_old='~/.keycloak/keycloak-${KEYCLOAK_OLD_VERSION}/bin/standalone.sh'" >> ~/.bashrc 

RUN source ${HOME}/.sdkman/bin/sdkman-init.sh \
    &&~/.keycloak/keycloak-${KEYCLOAK_OLD_VERSION}/bin/add-user-keycloak.sh --user ${KEYCLOAK_ADMIN} --password ${KEYCLOAK_ADMIN_PASSWORD}

# ---------------------------------- set up z ----------------------------------
RUN git clone https://github.com/rupa/z.git ~/.z-sh \
    && echo '. $HOME/.z-sh/z.sh' >> ~/.bashrc

USER root

# ---------------------------------- set up language ----------------------------------
# setup locale https://stackoverflow.com/a/55077451/13723015
ARG LOCALE
ENV LOCALE=${LOCALE}
ENV LC_ALL=${LOCALE}
ENV LANG=${LOCALE}

RUN apt-get -y install locales \
    && echo "LC_ALL=${LOCALE}" >> /etc/environment \
    && echo "${LOCALE} UTF-8" >> /etc/locale.gen \
    && echo "LANG=${LOCALE}" > /etc/locale.conf \
    && locale-gen ${LOCALE}

# ---------------------------------- addtional packages ----------------------------------

ARG ADDITONAL_PKG="iputils-ping lsof tree"

RUN apt-get update -y \
    && apt-get install ${ADDITONAL_PKG} -y \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*
