CREATE SEQUENCE IF NOT EXISTS datasource_error_log_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE datasource_error_log
(
    id BIGINT NOT NULL,
    CONSTRAINT pk_datasource_error_log PRIMARY KEY (id)
);