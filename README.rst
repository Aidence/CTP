Custom CTP
==========

We've added the option to save hash-original mappings to Redis to be able to de-anonymize study analysis responses on the
way back to the PACS in a proxy-cloud setup.


Configuration
=============

To use the mapping store, you need to set the ``USE_REDIS`` environment variable (this can be anything, as long as it's
set). Also, Redis configuration parameters can be set via environment variables::

     REDIS_HOST - default: 127.0.0.1
     REDIS_PORT - default: 6379
     REDIS_DB - default: 0
     REDIS_PASSWORD - no default password: null
     REDIS_TIMEOUT - default: 2000 - request timeout in seconds
     REDIS_TIME_TO_EXPIRE - default: 24 * 60 * 60 (1 day) - time in seconds for the keys to expire


Anonymization
=============

When ``USE_REDIS`` is set and the Redis environment variables are properly set and pointing to a running Redis server,
DICOM tags that have ``@hash`` or ``@hashuid`` as anonymizing method in ``source/files/scripts/AnonymizeIn.xml``
will have their hash and original value written to Redis (the hash is the key and the original value is the value).
These keys expire after the number of seconds as specified by ``REDIS_TIME_TO_EXPIRE``.


De-anonymization
================

On the way back to the PACS the generated responses can be (and most likely need to be) de-anonymized. This is done by
using ``@lookuporiginal`` in ``source/files/scripts/AnonymizeOut.xml`` for the DICOM tags you want to de-anonymize.
This method will fetch the original value based on the hash value.


StudyDate
=========

StudyDate values are currently anonymized by ``@incrementdate(this,@DATEOFFSET)``, which uses a fixed date offset for
all studies. This is because we need to know what the offset is in the Q/R proxy when requesting prior study
information. To make the offset unique for a patient and use ``@hashdate(this,PatientID)`` we need to be able to
de-anonymize the resulting anonymized date in the Q/R proxy.