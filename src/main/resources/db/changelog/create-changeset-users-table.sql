CREATE SEQUENCE users_seq START 1;

CREATE TABLE users (
    id BIGINT PRIMARY KEY DEFAULT nextval('users_seq'),
    name VARCHAR(255) NOT NULL
);