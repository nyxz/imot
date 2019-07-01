
CREATE EXTENSION "uuid-ossp";

CREATE TABLE properties (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    type            VARCHAR(200),
    area            VARCHAR(200) NOT NULL,
    address         VARCHAR(200),
    price           INT,
    raw_price       VARCHAR(50),
    description     TEXT,
    size            INT,
    raw_size        VARCHAR(50),
    url             VARCHAR(2083) NOT NULL UNIQUE,
    build_type      VARCHAR(200),
    build_year      INT,
    raw_floor       VARCHAR,
    floor           INT,
    total_floors    INT,
    seller_phone    VARCHAR(50),
    seller_name     VARCHAR(256),
    date_created    TIMESTAMP NOT NULL DEFAULT now(),
    date_modified   TIMESTAMP NOT NULL DEFAULT now()
);


CREATE TABLE areas (
    name            VARCHAR(200)    PRIMARY KEY,
    enabled         BOOLEAN         NOT NULL DEFAULT TRUE
);
