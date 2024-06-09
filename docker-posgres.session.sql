-- create schema
CREATE SCHEMA IF NOT EXISTS my_schema;

-- create table
CREATE TABLE
    IF NOT EXISTS my_schema."USER" (
        id SERIAL PRIMARY KEY,
        name VARCHAR(16) NOT NULL,
        age INT NOT NULL,
        profile VARCHAR(64) NOT NULL
    );

-- insert
INSERT INTO
    my_schema."USER" (name, age, profile)
SELECT
    'Ichiro',
    30,
    'Hello'
WHERE
    NOT EXISTS (
        SELECT
            1
        FROM
            my_schema."USER"
        WHERE
            name = 'Ichiro'
    );

INSERT INTO
    my_schema."USER" (name, age, profile)
SELECT
    'Jiro',
    25,
    'Hello'
WHERE
    NOT EXISTS (
        SELECT
            1
        FROM
            my_schema."USER"
        WHERE
            name = 'Jiro'
    );

INSERT INTO
    my_schema."USER" (name, age, profile)
SELECT
    'Saburo',
    20,
    'Hello'
WHERE
    NOT EXISTS (
        SELECT
            1
        FROM
            my_schema."USER"
        WHERE
            name = 'Saburo'
    );