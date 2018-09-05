FROM openjdk:8-jdk-alpine3.8 as builder

WORKDIR /build

RUN apk add --update \
  apache-ant \
  && \
  rm -rf /var/cache/apk/*

COPY . /build

RUN ant installer


FROM openjdk:8-jre-alpine3.8

LABEL maintainer="Aidence B.V. <info@aidence.com>"

RUN apk add --update \
  # Install a proper shell instead of Busybox
  bash \
  # For groupmod/usermod
  shadow \
  su-exec \
  wget \
  && \
  rm -rf /var/cache/apk/*

# Download and setup confd, used to generate configuration files from
# environment variables.
ARG CONFD_VERSION=0.16.0
RUN wget -O /usr/bin/confd https://github.com/kelseyhightower/confd/releases/download/v${CONFD_VERSION}/confd-${CONFD_VERSION}-linux-amd64 && \
  chmod +x /usr/bin/confd
COPY docker/confd /etc/confd

WORKDIR /opt/ctp

# Create app user and group
# These IDs can be overriden via $PUID and $PGID environment variables
# at runtime. See the entrypoint script.
RUN groupadd -g 1000 app && \
      useradd -m -u 1000 -g 1000 -s /bin/bash app

RUN mkdir /data && chown app:app /opt/ctp /data

USER app:app

COPY --from=builder --chown=app:app /build/products/CTP-installer.jar /tmp/install/

RUN unzip /tmp/install/CTP-installer.jar && \
  rm -rf /tmp/install

COPY --chown=app:app docker/run.sh /opt/ctp/run.sh

WORKDIR /opt/ctp/CTP

EXPOSE 1080 11111 11112

# Switch back to root, since the entrypoint script needs root
# privileges to change UID/GID at runtime. The script will drop down
# to an unprivileged user for running the application, however.
USER root

ENTRYPOINT ["/opt/ctp/run.sh"]
