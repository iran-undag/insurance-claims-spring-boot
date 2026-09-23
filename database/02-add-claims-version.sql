--we set version to handle concurrency via optimistic locking

\set ON_ERROR_STOP on

\connect insurance_claims_boot

BEGIN;

ALTER TABLE claims
ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

COMMIT;

\connect insurance_claims_boot_test

BEGIN;

ALTER TABLE claims
ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

COMMIT;
