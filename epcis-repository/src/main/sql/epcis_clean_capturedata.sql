-- Ooops, these files have been forgotten and are possibly outdated.

BEGIN;

DELETE FROM `event_AggregationEvent_EPCs`;
DELETE FROM `event_AggregationEvent`;
DELETE FROM `event_AggregationEvent_bizTrans`;
DELETE FROM `event_ObjectEvent_EPCs`;
DELETE FROM `event_ObjectEvent`;
DELETE FROM `event_QuantityEvent`;
DELETE FROM `event_TransactionEvent_bizTrans`;
DELETE FROM `event_TransactionEvent_EPCs`;
DELETE FROM `event_TransactionEvent`;

COMMIT;
