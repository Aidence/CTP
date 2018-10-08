#!/bin/bash

set -ex

check_env() {
    test $(printf "%s\n" "${!1}") || { echo "Required environment variable '$1' is not defined" 1>&2; exit 1; }
}

check_env CTP_AET

# Update user and group IDs to match the IDs passed from the host
PUID=${PUID:-1000}
PGID=${PGID:-1000}
groupmod -o -g "$PGID" app
usermod -o -u "$PUID" app
echo "Updated app user to UID=$PUID GID=$PGID"

# Allow switching confd template files
CTP_CONFIG_TEMPLATE="config-${CTP_CONFIG_TYPE:-single}.xml"
ln -sfn "/etc/confd/templates/$CTP_CONFIG_TEMPLATE" /etc/confd/templates/config.xml

# Update configuration from the environment
su-exec app:app confd -onetime -backend env

# Exclude certain DICOM tags from anonymization on import
if [ -n "$CTP_EXCLUDE_ANONYM" ]; then
    # This is a bit terse, but it sets en="F" (i.e. disables) the lines which
    # contain one of the specified tag names.
    # Bash's parameter expansion is used to replace spaces with literal '\|',
    # which is passed to `sed` to serve as a grouping "or".
    # The env var is expected to be a list of DICOM tags separated by space,
    # e.g. "PatientId AccessionNumber StudyInstanceUID".
    sed -i 's:\(.*\)en="T"\(.*\('${CTP_EXCLUDE_ANONYM// /\\|}'\).*\):\1en="F"\2:' \
        scripts/DicomServiceAnonymizer.script
    echo "CTP anonymization disabled for DICOM tags: $CTP_EXCLUDE_ANONYM"
fi

exec su-exec app:app java -jar Runner.jar
