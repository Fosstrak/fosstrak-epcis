-- deletes all data from all the tables in your epcis db

BEGIN;

DELETE FROM `bizTransaction`;
DELETE FROM `event_AggregationEvent`;
DELETE FROM `event_AggregationEvent_bizTrans`;
DELETE FROM `event_AggregationEvent_EPCs`;
DELETE FROM `event_ObjectEvent`;
DELETE FROM `event_ObjectEvent_bizTrans`;
DELETE FROM `event_ObjectEvent_EPCs`;
DELETE FROM `event_ObjectEvent_extensions`;
DELETE FROM `event_QuantityEvent`;
DELETE FROM `event_QuantityEvent_bizTrans`;
DELETE FROM `event_TransactionEvent`;
DELETE FROM `event_TransactionEvent_bizTrans`;
DELETE FROM `event_TransactionEvent_EPCs`;
DELETE FROM `subscription`;
DELETE FROM `voc_bizLoc`;
DELETE FROM `voc_bizStep`;
DELETE FROM `voc_bizTrans`;
DELETE FROM `voc_bizTransType`;
DELETE FROM `voc_disposition`;
DELETE FROM `voc_epcClass`;
DELETE FROM `voc_readPoint`;

COMMIT;
