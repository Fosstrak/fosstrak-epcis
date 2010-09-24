ALTER TABLE event_aggregationevent ADD COLUMN eventTimeMs BIGINT(20) NOT NULL AFTER eventTime;
ALTER TABLE event_aggregationevent ADD COLUMN recordTimeMs BIGINT(20) NOT NULL AFTER recordTime;
UPDATE event_aggregationevent SET eventTimeMs = UNIX_TIMESTAMP(eventTime) * 1000;
UPDATE event_aggregationevent SET recordTimeMs = UNIX_TIMESTAMP(recordTime) * 1000;

ALTER TABLE event_objectevent ADD COLUMN eventTimeMs BIGINT(20) NOT NULL AFTER eventTime;
ALTER TABLE event_objectevent ADD COLUMN recordTimeMs BIGINT(20) NOT NULL AFTER recordTime;
UPDATE event_objectevent SET eventTimeMs = UNIX_TIMESTAMP(eventTime) * 1000;
UPDATE event_objectevent SET recordTimeMs = UNIX_TIMESTAMP(recordTime) * 1000;

ALTER TABLE event_quantityevent ADD COLUMN eventTimeMs BIGINT(20) NOT NULL AFTER eventTime;
ALTER TABLE event_quantityevent ADD COLUMN recordTimeMs BIGINT(20) NOT NULL AFTER recordTime;
UPDATE event_quantityevent SET eventTimeMs = UNIX_TIMESTAMP(eventTime) * 1000;
UPDATE event_quantityevent SET recordTimeMs = UNIX_TIMESTAMP(recordTime) * 1000;

ALTER TABLE event_transactionevent ADD COLUMN eventTimeMs BIGINT(20) NOT NULL AFTER eventTime;
ALTER TABLE event_transactionevent ADD COLUMN recordTimeMs BIGINT(20) NOT NULL AFTER recordTime;
UPDATE event_transactionevent SET eventTimeMs = UNIX_TIMESTAMP(eventTime) * 1000;
UPDATE event_transactionevent SET recordTimeMs = UNIX_TIMESTAMP(recordTime) * 1000;

COMMIT;