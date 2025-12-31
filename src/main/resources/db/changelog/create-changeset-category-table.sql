CREATE SEQUENCE category_seq START 1;

CREATE TABLE category (
    id BIGINT PRIMARY KEY DEFAULT nextval('category_seq'),
    name VARCHAR(255) NOT NULL
);