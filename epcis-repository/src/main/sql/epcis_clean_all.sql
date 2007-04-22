-- Copyright (C) 2007 ETH Zurich
--
-- This file is part of Accada (www.accada.org).
--
-- Accada is free software; you can redistribute it and/or
-- modify it under the terms of the GNU Lesser General Public
-- License version 2.1, as published by the Free Software Foundation.
--
-- Accada is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
-- Lesser General Public License for more details.
--
-- You should have received a copy of the GNU Lesser General Public
-- License along with Accada; if not, write to the Free
-- Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
-- Boston, MA  02110-1301  USA


-- Deletes all data from all the tables in your EPCIS database

BEGIN;

DELETE FROM `bizTransaction`;
DELETE FROM `event_AggregationEvent`;
DELETE FROM `event_AggregationEvent_bizTrans`;
DELETE FROM `event_AggregationEvent_EPCs`;
DELETE FROM `event_AggregationEvent_extensions`;
DELETE FROM `event_ObjectEvent`;
DELETE FROM `event_ObjectEvent_bizTrans`;
DELETE FROM `event_ObjectEvent_EPCs`;
DELETE FROM `event_ObjectEvent_extensions`;
DELETE FROM `event_QuantityEvent`;
DELETE FROM `event_QuantityEvent_bizTrans`;
DELETE FROM `event_QuantityEvent_extensions`;
DELETE FROM `event_TransactionEvent`;
DELETE FROM `event_TransactionEvent_bizTrans`;
DELETE FROM `event_TransactionEvent_EPCs`;
DELETE FROM `event_TransactionEvent_extensions`;
DELETE FROM `subscription`;
DELETE FROM `voc_bizLoc`;
DELETE FROM `voc_bizLoc_attr`;
DELETE FROM `voc_bizStep`;
DELETE FROM `voc_bizStep_attr`;
DELETE FROM `voc_bizTrans`;
DELETE FROM `voc_bizTrans_attr`;
DELETE FROM `voc_bizTransType`;
DELETE FROM `voc_bizTransType_attr`;
DELETE FROM `voc_disposition`;
DELETE FROM `voc_disposition_attr`;
DELETE FROM `voc_epcClass`;
DELETE FROM `voc_epcClass_attr`;
DELETE FROM `voc_readPoint`;
DELETE FROM `voc_readPoint_attr`;
DELETE FROM `vocabularies`;

COMMIT;
