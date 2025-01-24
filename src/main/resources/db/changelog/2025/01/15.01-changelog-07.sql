-- Временный скрипт для генерации тестовых данных

TRUNCATE TABLE transaction CASCADE;
TRUNCATE TABLE account CASCADE;
TRUNCATE TABLE client CASCADE;

INSERT INTO client (id, first_name, last_name, middle_name)
SELECT
    generate_series(1, 100) AS id,
    'Имя' || generate_series(1, 100) AS first_name,
    'Фамилия' || generate_series(1, 100) AS last_name,
    'Отчество' || generate_series(1, 100) AS middle_name;

INSERT INTO account (id, client_id, balance_type, balance)
SELECT
    generate_series(1, 100) AS id,
    client.id,
    CASE WHEN random() < 0.5 THEN 'DEBIT' ELSE 'CREDIT' END AS balance_type,
    random() * 10000 AS balance
FROM
    client
ORDER BY random()
LIMIT 100;

INSERT INTO transaction (id, account_id, amount, time)
SELECT
    generate_series(1, 300) AS id,
    account.id,
    random() * 1000 AS amount,
    NOW() - (random() * interval '30 days') AS time
FROM
    account
ORDER BY random()
LIMIT 300;

ALTER SEQUENCE client_seq RESTART WITH 101;
ALTER SEQUENCE account_seq RESTART WITH 101;
ALTER SEQUENCE transaction_seq RESTART WITH 301;