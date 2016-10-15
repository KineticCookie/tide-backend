# Sessions schema

# --- !Ups

CREATE TABLE Sessions (
    id UUID NOT NULL,
    token UUID NOT NULL,
    PRIMARY KEY (id),
    UNIQUE(token)
);

# --- !Downs

DROP TABLE Sessions;