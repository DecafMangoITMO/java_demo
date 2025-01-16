ALTER TABLE account
    ADD client_id BIGINT REFERENCES client (id) ON DELETE CASCADE;
ALTER TABLE account
    ADD balance_type VARCHAR(255);
ALTER TABLE account
    ADD balance DOUBLE PRECISION;
