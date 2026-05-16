CREATE TABLE organizations (
    id                  BIGSERIAL PRIMARY KEY,
    name                VARCHAR(255) NOT NULL,
    tax_compliance_name VARCHAR(255) NOT NULL,
    pan_number          VARCHAR(20),
    gst_number          VARCHAR(20),
    tan_number          VARCHAR(20),
    created_at          TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE users (
    id              BIGSERIAL PRIMARY KEY,
    full_name       VARCHAR(255) NOT NULL,
    email           VARCHAR(255) UNIQUE,
    mobile_number   VARCHAR(20)  UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    organization_id BIGINT       NOT NULL REFERENCES organizations (id),
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);
