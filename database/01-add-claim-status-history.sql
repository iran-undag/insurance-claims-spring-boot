\set ON_ERROR_STOP on

\connect insurance_claims_boot

BEGIN;

CREATE TABLE claim_status_history (
  id BIGSERIAL PRIMARY KEY,
  claim_id BIGINT NOT NULL,
  status VARCHAR(20) NOT NULL,
  changed_at TIMESTAMP NOT NULL,
  CONSTRAINT claim_status_history_claim_fk
      FOREIGN KEY (claim_id) REFERENCES claims(id) ON DELETE CASCADE
);

CREATE INDEX claim_status_history_claim_changed_idx
    ON claim_status_history (claim_id, changed_at, id);

INSERT INTO claim_status_history (claim_id, status, changed_at)
SELECT id, status, created_at
FROM claims;

COMMIT;

\connect insurance_claims_boot_test

BEGIN;

CREATE TABLE claim_status_history (
  id BIGSERIAL PRIMARY KEY,
  claim_id BIGINT NOT NULL,
  status VARCHAR(20) NOT NULL,
  changed_at TIMESTAMP NOT NULL,
  CONSTRAINT claim_status_history_claim_fk
      FOREIGN KEY (claim_id) REFERENCES claims(id) ON DELETE CASCADE
);

CREATE INDEX claim_status_history_claim_changed_idx
    ON claim_status_history (claim_id, changed_at, id);

INSERT INTO claim_status_history (claim_id, status, changed_at)
SELECT id, status, created_at
FROM claims;

COMMIT;
