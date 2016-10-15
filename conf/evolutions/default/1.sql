# Persons schema

# --- !Ups

CREATE TABLE Users (
    id UUID NOT NULL,
    fullname VARCHAR(255),
    email VARCHAR(255),
    pswd VARCHAR(255),
    PRIMARY KEY (id),
    UNIQUE(email)
);

# --- !Downs

DROP TABLE Users;