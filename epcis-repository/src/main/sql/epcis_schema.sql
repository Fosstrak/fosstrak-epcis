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


-- This is the EPCIS database schema file for the creation of the
-- tables in MySQL. Note: this schema is MySQL specific!


BEGIN;

SET storage_engine=INNODB;


-- ---------------------------------------------
-- Vocabularies
-- ---------------------------------------------

CREATE TABLE `voc_BizLoc` (
`id` bigint PRIMARY KEY auto_increment, -- id auto_increment
`uri` varchar(1023) NOT NULL 
);

CREATE TABLE `voc_BizLoc_attr` (
`id` bigint NOT NULL REFERENCES `voc_BizLoc`(`id`),
`attribute` varchar(1023) NOT NULL,
`value` varchar(1023) NOT NULL
);

CREATE TABLE `voc_BizStep` (
`id` bigint PRIMARY KEY auto_increment, -- id auto_increment
`uri` varchar(1023) NOT NULL 
);

CREATE TABLE `voc_BizStep_attr` (
`id` bigint NOT NULL REFERENCES `voc_BizStep`(`id`),
`attribute` varchar(1023) NOT NULL,
`value` varchar(1023) NOT NULL
);

CREATE TABLE `voc_BizTransType` (
`id` bigint PRIMARY KEY auto_increment, -- id auto_increment
`uri` varchar(1023) NOT NULL 
);

CREATE TABLE `voc_BizTransType_attr` (
`id` bigint NOT NULL REFERENCES `voc_BizTransType`(`id`),
`attribute` varchar(1023) NOT NULL,
`value` varchar(1023) NOT NULL
);

CREATE TABLE `voc_BizTrans` (
`id` bigint PRIMARY KEY auto_increment, -- id auto_increment
`uri` varchar(1023) NOT NULL 
);

CREATE TABLE `voc_BizTrans_attr` (
`id` bigint NOT NULL REFERENCES `voc_BizTrans`(`id`),
`attribute` varchar(1023) NOT NULL,
`value` varchar(1023) NOT NULL
);

CREATE TABLE `voc_Disposition` (
`id` bigint PRIMARY KEY auto_increment, -- id auto_increment
`uri` varchar(1023) NOT NULL 
);

CREATE TABLE `voc_Disposition_attr` (
`id` bigint NOT NULL REFERENCES `voc_Disposition`(`id`),
`attribute` varchar(1023) NOT NULL,
`value` varchar(1023) NOT NULL
);

CREATE TABLE `voc_ReadPoint` (
`id` bigint PRIMARY KEY auto_increment, -- id auto_increment
`uri` varchar(1023) NOT NULL 
);

CREATE TABLE `voc_ReadPoint_attr` (
`id` bigint NOT NULL REFERENCES `voc_ReadPoint`(`id`),
`attribute` varchar(1023) NOT NULL,
`value` varchar(1023) NOT NULL
);

CREATE TABLE `voc_EPCClass` (
`id` bigint PRIMARY KEY auto_increment, -- id auto_increment
`uri` varchar(1023) NOT NULL 
);

CREATE TABLE `voc_EPCClass_attr` (
`id` bigint NOT NULL REFERENCES `voc_EPCClass`(`id`),
`attribute` varchar(1023) NOT NULL,
`value` varchar(1023) NOT NULL
);


-- ---------------------------------------------
-- Non-Standard Masterdata Vocabularies
-- ---------------------------------------------

CREATE TABLE `voc_Any` (
`id` bigint PRIMARY KEY auto_increment, -- id auto_increment
`uri` varchar(1023) NOT NULL,
`vtype` varchar(1023) NOT NULL
);

CREATE TABLE `voc_Any_attr` (
`id` bigint NOT NULL REFERENCES `voc_Any`(`id`),
`attribute` varchar(1023) NOT NULL,
`value` varchar(1023) NOT NULL
);


-- ---------------------------------------------
-- Business Transactions
-- ---------------------------------------------

CREATE TABLE `BizTransaction` (
`id` bigint PRIMARY KEY auto_increment, -- id auto_increment
`bizTrans` bigint NOT NULL REFERENCES `voc_BizTrans` (`id`),
`type` bigint REFERENCES `voc_BizTransType` (`id`)
);


-- ---------------------------------------------
-- Aggregation Events
-- ---------------------------------------------

CREATE TABLE `event_AggregationEvent` (
`id` bigint PRIMARY KEY auto_increment, -- id auto_increment
`eventTime` timestamp NOT NULL,
`eventTimeMs` bigint(20) NOT NULL,
`recordTime` timestamp NOT NULL,
`recordTimeMs` bigint(20) NOT NULL,
`eventTimeZoneOffset` varchar(8) NOT NULL,
`parentID` varchar(1023) DEFAULT NULL, -- varchar(1023) good choice?
`action` varchar(8) NOT NULL CHECK (`action` IN ('ADD','OBSERVE','DELETE')),
`bizStep` bigint DEFAULT NULL REFERENCES `voc_BizStep` (`id`),
`disposition` bigint DEFAULT NULL REFERENCES `voc_Disposition`(`id`),
`readPoint` bigint DEFAULT NULL REFERENCES `voc_ReadPoint`(`id`),
`bizLocation` bigint DEFAULT NULL REFERENCES `voc_BizLoc`(`id`)
-- `bizTransaction` bigint DEFAULT NULL REFERENCES `voc_BizTrans`(`id`) 
);

CREATE TABLE `event_AggregationEvent_EPCs` (
-- This EPC list is called childEPCs (or childEPCList) in the standard,
-- for uniform access `child` has been ommitted from the table name.
`event_id` bigint NOT NULL REFERENCES `event_AggregationEvent`,
`epc` varchar(1023) NOT NULL,
`idx` int NOT NULL,
INDEX (event_id)
);

CREATE TABLE `event_AggregationEvent_bizTrans` ( 
-- bizTrans 0..* associated with event
`event_id` bigint NOT NULL REFERENCES `event_AggregationEvent` (`id`),
`bizTrans_id` bigint NOT NULL REFERENCES `BizTransaction` (`id`),
`idx` int NOT NULL,
INDEX (event_id)
);

CREATE TABLE `event_AggregationEvent_extensions` (
`id` bigint PRIMARY KEY auto_increment,
`event_id` bigint NOT NULL REFERENCES `event_AggregationEvent` (`id`),
`fieldname` varchar(128) NOT NULL,
`prefix` varchar(32) NOT NULL,
`intValue` integer,
`floatValue` float,
`dateValue` timestamp NULL DEFAULT NULL,
`strValue` varchar(1024),
INDEX (event_id)
);


-- ---------------------------------------------
-- Object Events
-- ---------------------------------------------

CREATE TABLE `event_ObjectEvent` (
`id` bigint PRIMARY KEY auto_increment, -- id auto_increment -> cross platform sequence???
`eventTime` timestamp NOT NULL,
`eventTimeMs` bigint(20) NOT NULL,
`recordTime` timestamp NOT NULL,
`recordTimeMs` bigint(20) NOT NULL,
`eventTimeZoneOffset` varchar(8) NOT NULL,
`action` varchar(8) NOT NULL CHECK (`action` IN ('ADD','OBSERVE','DELETE')),
`bizStep` bigint DEFAULT NULL REFERENCES `voc_BizStep` (`id`),
`disposition` bigint DEFAULT NULL REFERENCES `voc_Disposition` (`id`),
`readPoint` bigint DEFAULT NULL REFERENCES `voc_ReadPoint` (`id`),
`bizLocation` bigint DEFAULT NULL REFERENCES `voc_BizLoc` (`id`)
-- `bizTransaction` bigint DEFAULT NULL REFERENCES `voc_BizTrans` (`id`)
);

CREATE TABLE `event_ObjectEvent_EPCs` (
`event_id` bigint NOT NULL REFERENCES `event_ObjectEvent`,
`epc` varchar(1023) NOT NULL,
`idx` int NOT NULL,
INDEX (event_id)
);

CREATE TABLE `event_ObjectEvent_bizTrans` ( 
-- bizTrans 0..* associated with event
`event_id` bigint NOT NULL REFERENCES `event_ObjectEvent` (`id`),
`bizTrans_id` bigint NOT NULL REFERENCES `BizTransaction` (`id`),
`idx` int NOT NULL,
INDEX (event_id)
);

CREATE TABLE `event_ObjectEvent_extensions` (
`id` bigint PRIMARY KEY auto_increment,
`event_id` bigint NOT NULL REFERENCES `event_ObjectEvent` (`id`),
`fieldname` varchar(128) NOT NULL,
`prefix` varchar(32) NOT NULL,
`intValue` integer,
`floatValue` float,
`dateValue` timestamp NULL DEFAULT NULL,
`strValue` varchar(1024),
INDEX (event_id)
);


-- ---------------------------------------------
-- Quantity Events
-- ---------------------------------------------

CREATE TABLE `event_QuantityEvent` (
`id` bigint PRIMARY KEY auto_increment, -- id auto_increment
`eventTime` timestamp NOT NULL,
`eventTimeMs` bigint(20) NOT NULL,
`recordTime` timestamp NOT NULL,
`recordTimeMs` bigint(20) NOT NULL,
`eventTimeZoneOffset` varchar(8) NOT NULL,
`epcClass` bigint NOT NULL REFERENCES `voc_EPCClass` (`id`),
`quantity` bigint NOT NULL,
`bizStep` bigint DEFAULT NULL REFERENCES `voc_BizStep` (`id`),
`disposition` bigint DEFAULT NULL REFERENCES `voc_Disposition` (`id`),
`readPoint` bigint DEFAULT NULL REFERENCES `voc_ReadPoint` (`id`),
`bizLocation` bigint DEFAULT NULL REFERENCES `voc_BizLoc` (`id`)
);

CREATE TABLE `event_QuantityEvent_bizTrans` ( 
-- bizTrans 0..* associated with event
`event_id` bigint NOT NULL REFERENCES `event_QuantityEvent` (`id`),
`bizTrans_id` bigint NOT NULL REFERENCES `BizTransaction` (`id`),
`idx` int NOT NULL,
INDEX (event_id)
);

CREATE TABLE `event_QuantityEvent_extensions` (
`id` bigint PRIMARY KEY auto_increment,
`event_id` bigint NOT NULL REFERENCES `event_QuantityEvent` (`id`),
`fieldname` varchar(128) NOT NULL,
`prefix` varchar(32) NOT NULL,
`intValue` integer,
`floatValue` float,
`dateValue` timestamp NULL DEFAULT NULL,
`strValue` varchar(1024),
INDEX (event_id)
);


-- ---------------------------------------------
-- Transaction Events
-- ---------------------------------------------

CREATE TABLE `event_TransactionEvent` (
`id` bigint PRIMARY KEY auto_increment, -- id auto_increment
`eventTime` timestamp NOT NULL,
`eventTimeMs` bigint(20) NOT NULL,
`recordTime` timestamp NOT NULL,
`recordTimeMs` bigint(20) NOT NULL,
`eventTimeZoneOffset` varchar(8) NOT NULL,
`parentID` varchar(1023) DEFAULT NULL,
`action` varchar(8) NOT NULL CHECK (`action` IN ('ADD','OBSERVE','DELETE')),
`bizStep` bigint DEFAULT NULL REFERENCES `voc_BizStep` (`id`),
`disposition` bigint DEFAULT NULL REFERENCES `voc_Disposition` (`id`),
`readPoint` bigint DEFAULT NULL REFERENCES `voc_ReadPoint` (`id`),
`bizLocation` bigint DEFAULT NULL REFERENCES `voc_BizLoc` (`id`)
-- `bizTransaction` bigint DEFAULT NULL REFERENCES `voc_BizTrans` (`id`)
);

CREATE TABLE `event_TransactionEvent_EPCs` (
`event_id` bigint NOT NULL REFERENCES `event_TransactionEvent`,
`epc` varchar(1023) NOT NULL,
`idx` int NOT NULL,
INDEX (event_id)
);

CREATE TABLE `event_TransactionEvent_bizTrans` ( 
-- bizTrans 1..* associated with event, at least one not yet enforced in DB
`event_id` bigint NOT NULL REFERENCES `event_TransactionEvent` (`id`),
`bizTrans_id` bigint NOT NULL REFERENCES `BizTransaction` (`id`),
`idx` int NOT NULL,
INDEX (event_id)
);

CREATE TABLE `event_TransactionEvent_extensions` (
`id` bigint PRIMARY KEY auto_increment,
`event_id` bigint NOT NULL REFERENCES `event_TransactionEvent` (`id`),
`fieldname` varchar(128) NOT NULL,
`prefix` varchar(32) NOT NULL,
`intValue` integer,
`floatValue` float,
`dateValue` timestamp NULL DEFAULT NULL,
`strValue` varchar(1024),
INDEX (event_id)
);


-- ---------------------------------------------
-- Subscriptions
-- ---------------------------------------------

CREATE TABLE subscription (
subscriptionid varchar(767) NOT NULL PRIMARY KEY,
params blob,
dest varchar(1023),
sched blob,
trigg varchar(1023),
initialrecordingtime timestamp,
exportifempty boolean,
queryname varchar(1023),
lastexecuted timestamp
);


COMMIT;

