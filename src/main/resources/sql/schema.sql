
CREATE EXTENSION "uuid-ossp";

CREATE TABLE properties (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    type            VARCHAR(200),
    area            VARCHAR(200) NOT NULL,
    address         VARCHAR(200),
    price           INT,
    raw_price        VARCHAR(50),
    description     TEXT,
    size            INT,
    raw_size         VARCHAR(50),
    url             VARCHAR(2083) NOT NULL,
    build_type       VARCHAR(200),
    build_year       INT,
    date_created     TIMESTAMP NOT NULL DEFAULT now(),
    date_modified    TIMESTAMP NOT NULL DEFAULT now()
);

