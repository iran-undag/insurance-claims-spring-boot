\set ON_ERROR_STOP on

\connect insurance_claims_boot

BEGIN;

ALTER TABLE claims
ADD COLUMN incident_location VARCHAR(200);

COMMIT;

\connect insurance_claims_boot_test

BEGIN;

ALTER TABLE claims
ADD COLUMN incident_location VARCHAR(200);

COMMIT;
