ALTER TABLE transaction
    ADD account_id BIGINT REFERENCES account (id) ON DELETE CASCADE;
ALTER TABLE transaction
    ADD amount DOUBLE PRECISION;
ALTER TABLE transaction
    ADD time TIMESTAMP;