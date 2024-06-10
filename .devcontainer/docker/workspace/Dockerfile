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

# ---------------------------------- enable git ----------------------------------

# https://github.com/devcontainers/cli/issues/98
# change to main branch https://dev-yakuza.posstree.com/git/default-branch/
# RUN git config --global --add safe.directory "${HOME}/workspace" \
#     && git config --global init.defaultBranch main \
#     && git config --global core.hooksPath "${HOME}/workspace/.githooks"

# ---------------------------------- sdkman for java ----------------------------------
# https://sdkman.io/install
ARG JAVA_VERSION
ARG GRADLE_VERSION

RUN curl -s "https://get.sdkman.io" | bash \
    && source "${HOME}/.sdkman/bin/sdkman-init.sh" \
    && sdk install java "${JAVA_VERSION}" \
    && sdk install gradle ${GRADLE_VERSION} \
    && sdk install kotlin ${KOTLIN_VERSION}

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

# ---------------------------------- aws cli ----------------------------------

ARG ZIP_FILE=awscliv2.zip
ARG BE_PATH_TO_COMPLETER=/usr/local/bin

RUN apt-get install -y less \
    && curl https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip -o ${ZIP_FILE} \
    && unzip ${ZIP_FILE} \
    && ./aws/install \
    && rm -f ${ZIP_FILE} \
    && echo 'export PATH="${BE_PATH_TO_COMPLETER}:${PATH}"' >> ${HOME}/.bashrc \
    && source ${HOME}/.bashrc \
    && complete -C "${BE_PATH_TO_COMPLETER}/aws_completer" aws

# ---------------------------------- github cli ----------------------------------

RUN curl -fsSL https://cli.github.com/packages/githubcli-archive-keyring.gpg | dd of=/usr/share/keyrings/githubcli-archive-keyring.gpg \
    && chmod go+r /usr/share/keyrings/githubcli-archive-keyring.gpg \
    && echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/githubcli-archive-keyring.gpg] https://cli.github.com/packages stable main" | tee /etc/apt/sources.list.d/github-cli.list > /dev/null \
    && apt-get update \
    && apt-get install gh -y \
    && gh --version

# ---------------------------------- addtional packages ----------------------------------

ARG ADDITONAL_PKG="iputils-ping lsof tree"

RUN apt-get update -y \
    && apt-get install ${ADDITONAL_PKG} -y \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*