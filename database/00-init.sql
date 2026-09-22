CREATE DATABASE insurance_claims_boot;
CREATE DATABASE insurance_claims_boot_test;

\connect insurance_claims_boot

CREATE TABLE claims (
  id BIGSERIAL PRIMARY KEY,
  claim_number VARCHAR(30) NOT NULL UNIQUE,
  policy_number VARCHAR(30) NOT NULL,
  claimant_name VARCHAR(100) NOT NULL,
  incident_date DATE NOT NULL,
  claim_type VARCHAR(20) NOT NULL,
  claimed_amount NUMERIC(15, 2) NOT NULL
      CHECK (claimed_amount > 0),
  status VARCHAR(20) NOT NULL,
  description VARCHAR(500) NOT NULL,
  created_at TIMESTAMP NOT NULL
);

\connect insurance_claims_boot_test

CREATE TABLE claims (
  id BIGSERIAL PRIMARY KEY,
  claim_number VARCHAR(30) NOT NULL UNIQUE,
  policy_number VARCHAR(30) NOT NULL,
  claimant_name VARCHAR(100) NOT NULL,
  incident_date DATE NOT NULL,
  claim_type VARCHAR(20) NOT NULL,
  claimed_amount NUMERIC(15, 2) NOT NULL
      CHECK (claimed_amount > 0),
  status VARCHAR(20) NOT NULL,
  description VARCHAR(500) NOT NULL,
  created_at TIMESTAMP NOT NULL
);
