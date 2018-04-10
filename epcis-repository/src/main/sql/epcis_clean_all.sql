-- Copyright (C) 2007 ETH Zurich
--
-- This file is part of Fosstrak (www.fosstrak.org).
--
-- Fosstrak is free software; you can redistribute it and/or
-- modify it under the terms of the GNU Lesser General Public
-- License version 2.1, as published by the Free Software Foundation.
--
-- Fosstrak is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
-- Lesser General Public License for more details.
--
-- You should have received a copy of the GNU Lesser General Public
-- License along with Fosstrak; if not, write to the Free
-- Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
-- Boston, MA  02110-1301  USA


-- Deletes all data from all the tables in your EPCIS database

BEGIN;

DELETE FROM `BizTransaction`;
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
DELETE FROM `voc_BizLoc`;
DELETE FROM `voc_BizLoc_attr`;
DELETE FROM `voc_BizStep`;
DELETE FROM `voc_BizStep_attr`;
DELETE FROM `voc_BizTrans`;
DELETE FROM `voc_BizTrans_attr`;
DELETE FROM `voc_BizTransType`;
DELETE FROM `voc_BizTransType_attr`;
DELETE FROM `voc_Disposition`;
DELETE FROM `voc_Disposition_attr`;
DELETE FROM `voc_EPCClass`;
DELETE FROM `voc_EPCClass_attr`;
DELETE FROM `voc_ReadPoint`;
DELETE FROM `voc_ReadPoint_attr`;
DELETE FROM `voc_Any`;
DELETE FROM `voc_Any_attr`;

COMMIT;
