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


-- This script inserts some test data to play around with into the EPCIS database.

BEGIN;

-- ---------------------------------------------
-- Business Transaction Test Data
-- ---------------------------------------------

INSERT INTO `BizTransaction` (`id`, `bizTrans`, `type`) VALUES
(1, 1, 1),
(2, 2, 1),
(3, 3, 2);


-- ---------------------------------------------
-- Aggregation Event Test Data
-- ---------------------------------------------

INSERT INTO `event_AggregationEvent` (`id`, `eventTime`, `recordTime`, `eventTimeZoneOffset`, `parentID`, `action`, `bizStep`, `disposition`, `readPoint`, `bizLocation`) VALUES
(1, '2006-06-01 15:55:04', '2006-08-18 13:50:07', '+01:00', 'urn:epc:id:sscc:0614141.1234567890', 'ADD', 13, 6, 3, 1);

INSERT INTO `event_AggregationEvent_EPCs` (`event_id`, `epc`, `idx`) VALUES
(1, 'urn:epc:id:sgtin:0057000.123430.2025', 0),
(1, 'urn:epc:id:sgtin:0057000.123430.2027', 1),
(1, 'urn:epc:id:sgtin:0057000.123430.2028', 2);

INSERT INTO `event_AggregationEvent_bizTrans` (`event_id`, `bizTrans_id`, `idx`) VALUES
(1, 2, 0),
(1, 3, 1);

INSERT INTO `event_AggregationEvent_extensions` (`id`, `event_id`, `fieldname`, `prefix`, `intValue`) VALUES
(1, 1, 'http://www.example.com/epcis/extensions/#temperature', 'xyz', 25);


-- ---------------------------------------------
-- Object Event Test Data
-- ---------------------------------------------

INSERT INTO `event_ObjectEvent` (`id`, `eventTime`, `recordTime`, `eventTimeZoneOffset`, `action`, `bizStep`, `disposition`, `readPoint`, `bizLocation`) VALUES
(1, '2006-05-10 04:50:35', '2006-08-18 13:50:11', '+01:00', 'OBSERVE', 14, 14, 4, 3),
(2, '2006-05-09 21:01:44', '2006-08-18 13:50:16', '+01:00', 'OBSERVE', 10, 7, 5, 4),
(3, '2006-04-03 22:36:17', '2006-08-18 13:50:21', '+01:00', 'ADD', 3, 1, 6, 5),
(4, '2006-04-03 20:33:31', '2006-08-18 13:50:26', '+01:00', 'ADD', 3, NULL, 7, 5),
(5, '2006-05-10 21:38:46', '2006-09-01 00:05:37', '+01:00', 'ADD', 3, NULL, 8, 6),
(6, '2006-05-21 09:21:40', '2006-09-01 00:06:03', '+01:00', 'OBSERVE', 14, NULL, 9, 7),
(7, '2006-06-27 16:55:08', '2006-09-01 00:06:16', '+01:00', 'OBSERVE', 14, NULL, 10, 8),
(8, '2006-06-16 14:28:23', '2006-09-01 00:07:46', '+01:00', 'ADD', 3, NULL, 8, 6),
(9, '2006-08-13 17:07:50', '2006-09-01 00:09:37', '+01:00', 'OBSERVE', 14, NULL, 11, 9),
(10, '2006-09-15 11:45:06', '2006-09-01 00:10:12', '+01:00', 'OBSERVE', 14, NULL, 10, 8),
(11, '2007-04-23 12:26:52', '2007-04-23 12:26:52', '+01:00', 'OBSERVE', 17, NULL, 5, 4);

INSERT INTO `event_ObjectEvent_EPCs` (`event_id`, `epc`, `idx`) VALUES
(1, 'urn:epc:id:sgtin:0034000.987650.2686', 0),
(2, 'urn:epc:id:sgtin:0034000.987650.2686', 1),
(2, 'urn:epc:id:sgtin:0034000.987650.3542', 2),
(3, 'urn:epc:id:sgtin:0057000.123780.7788', 3),
(4, 'urn:epc:id:sgtin:0057000.123780.3167', 4),
(5, 'urn:epc:id:sgtin:0000001.000001.0001', 5),
(6, 'urn:epc:id:sgtin:0000001.000001.0001', 6),
(7, 'urn:epc:id:sgtin:0000001.000001.0001', 7),
(8, 'urn:epc:id:sgtin:0000001.000001.0002', 8),
(9, 'urn:epc:id:sgtin:0000001.000001.0002', 9),
(10, 'urn:epc:id:sgtin:0000001.000001.0002', 10),
(11, 'urn:epc:id:sgtin:0057000.123430.2028', 11);

INSERT INTO `event_ObjectEvent_bizTrans` (`event_id`, `bizTrans_id`, `idx`) VALUES
(2, 2, 0);

INSERT INTO `event_ObjectEvent_extensions` (`id`, `event_id`, `fieldname`, `prefix`, `intValue`, `floatValue`, `strValue`) VALUES
(1, 1, 'http://www.example.com/epcis/extensions/#temperature', 'xyz', 21, NULL, NULL),
(2, 4, 'http://www.example.com/epcis/extensions/#origin', 'abc', NULL, NULL, 'Germany'),
(3, 5, 'http://www.example.com/epcis/extensions/#cost', 'uvw', NULL, 4.5, NULL);


-- ---------------------------------------------
-- Quantity Event Test Data
-- ---------------------------------------------

INSERT INTO `event_QuantityEvent` (`id`, `eventTime`, `recordTime`, `eventTimeZoneOffset`, `epcClass`, `quantity`, `bizStep`, `disposition`, `readPoint`, `bizLocation`) VALUES
(1, '2006-08-10 18:14:00', '2006-08-18 13:49:56', '+01:00', 1, 1000, NULL, NULL, 1, 1),
(2, '2006-01-15 16:15:31', '2006-08-18 13:50:00', '+01:00', 2, 67, 20, 1, 2, 2);


-- ---------------------------------------------
-- Transaction Event Test Data
-- ---------------------------------------------

INSERT INTO `event_TransactionEvent` (`id`, `eventTime`, `recordTime`, `eventTimeZoneOffset`, `parentID`, `action`, `bizStep`, `disposition`, `readPoint`, `bizLocation`) VALUES
(1, '2006-08-20 07:03:51', '2006-08-18 13:49:47', '+01:00', NULL, 'DELETE', NULL, NULL, NULL, NULL),
(2, '2006-08-18 11:53:01', '2006-08-18 13:49:53', '+01:00', NULL, 'ADD', NULL, NULL, NULL, NULL);

INSERT INTO `event_TransactionEvent_EPCs` (`event_id`, `epc`, `idx`) VALUES
(1, 'urn:epc:id:sgtin:0057000.678930.5003', 0),
(1, 'urn:epc:id:sgtin:0057000.678930.5004', 1),
(2, 'urn:epc:id:sgtin:0057000.678930.5003', 2),
(2, 'urn:epc:id:sgtin:0057000.678930.5004', 3);

INSERT INTO `event_TransactionEvent_bizTrans` (`event_id`, `bizTrans_id`, `idx`) VALUES
(1, 1, 0),
(2, 1, 1);


-- ---------------------------------------------
-- Vocabularies Test Data
-- ---------------------------------------------

INSERT INTO `voc_BizLoc` (`id`, `uri`) VALUES
(1, 'urn:epc:id:sgln:0614141.00729.whatever450'),
(2, 'urn:epc:id:sgln:0614141.00102.loc007'),
(3, 'urn:epc:id:sgln:0614141.00729.loc217'),
(4, 'urn:epc:id:sgln:0614141.00729.loc215'),
(5, 'urn:epc:id:sgln:0614141.00729.shipping'),
(6, 'http://epcis.fosstrak.org/demo/loc/usa/morristown'),
(7, 'http://epcis.fosstrak.org/demo/loc/usa/baltimore'),
(8, 'http://epcis.fosstrak.org/demo/loc/usa/newport'),
(9, 'http://epcis.fosstrak.org/demo/loc/germany/hamburg');

INSERT INTO `voc_BizStep` (`id`, `uri`) VALUES
(1, 'urn:epcglobal:cbv:bizstep:accepting'),
(2, 'urn:epcglobal:cbv:bizstep:arriving'),
(3, 'urn:epcglobal:cbv:bizstep:commissioning'),
(4, 'urn:epcglobal:cbv:bizstep:decommissioning'),
(5, 'urn:epcglobal:cbv:bizstep:departing'),
(6, 'urn:epcglobal:cbv:bizstep:destroying'),
(7, 'urn:epcglobal:cbv:bizstep:encoding'),
(8, 'urn:epcglobal:cbv:bizstep:holding'),
(9, 'urn:epcglobal:cbv:bizstep:inspecting'),
(10, 'urn:epcglobal:cbv:bizstep:loading'),
(11, 'urn:epcglobal:cbv:bizstep:other'),
(12, 'urn:epcglobal:cbv:bizstep:packing'),
(13, 'urn:epcglobal:cbv:bizstep:picking'),
(14, 'urn:epcglobal:cbv:bizstep:receiving'),
(15, 'urn:epcglobal:cbv:bizstep:repackaging'),
(16, 'urn:epcglobal:cbv:bizstep:reserving'),
(17, 'urn:epcglobal:cbv:bizstep:shipping'),
(18, 'urn:epcglobal:cbv:bizstep:staging_outbound'),
(19, 'urn:epcglobal:cbv:bizstep:stocking'),
(20, 'urn:epcglobal:cbv:bizstep:storing');

INSERT INTO `voc_BizTrans` (`id`, `uri`) VALUES
(1, 'http://transaction.acme.com/tracker/6677150'),
(2, 'http://transaction.acme.com/po/12345678'),
(3, 'http://transaction.acme.com/asn/1152');

INSERT INTO `voc_BizTransType` (`id`, `uri`) VALUES
(1, 'urn:epcglobal:cbv:fmcg:btt:po'),
(2, 'urn:epcglobal:cbv:btt:asn');

INSERT INTO `voc_Disposition` (`id`, `uri`) VALUES
(1, 'urn:epcglobal:cbv:disp:active'),
(2, 'urn:epcglobal:cbv:disp:container_closed'),
(3, 'urn:epcglobal:cbv:disp:destroyed'),
(4, 'urn:epcglobal:cbv:disp:encoded'),
(5, 'urn:epcglobal:cbv:disp:inactive'),
(6, 'urn:epcglobal:cbv:disp:in_progress'),
(7, 'urn:epcglobal:cbv:disp:in_transit'),
(8, 'urn:epcglobal:cbv:disp:non_sellable_expired'),
(9, 'urn:epcglobal:cbv:disp:non_sellable_damaged'),
(10, 'urn:epcglobal:cbv:disp:non_sellable_no_pedigree_match'),
(11, 'urn:epcglobal:cbv:disp:non_sellable_other'),
(12, 'urn:epcglobal:cbv:disp:non_sellable_recalled'),
(13, 'urn:epcglobal:cbv:disp:reserved'),
(14, 'urn:epcglobal:cbv:disp:returned'),
(15, 'urn:epcglobal:cbv:disp:sellable_accessible'),
(16, 'urn:epcglobal:cbv:disp:sellable_not_accessible'),
(17, 'urn:epcglobal:cbv:disp:sold'),
(18, 'urn:epcglobal:cbv:disp:unknown');

INSERT INTO `voc_EPCClass` (`id`, `uri`) VALUES
(1, 'urn:epc:id:sgtin:0069000.919923'),
(2, 'urn:epc:id:sgtin:0069000.957110');

INSERT INTO `voc_ReadPoint` (`id`, `uri`) VALUES
(1, 'urn:epc:id:sgln:0614141.00729.loc451'),
(2, 'urn:epc:id:sgln:0614141.00102.loc014'),
(3, 'urn:epc:id:sgln:0614141.00729.shipping-door1'),
(4, 'urn:epc:id:sgln:0614141.00729.whatever217'),
(5, 'urn:epc:id:sgln:0614141.00729.whatever215'),
(6, 'urn:epc:id:sgln:0614141.00729.shipping-door2'),
(7, 'urn:epc:id:sgln:0614141.00729.shipping-door3'),
(8, 'http://epcis.fosstrak.org/demo/loc/usa/morristown'),
(9, 'http://epcis.fosstrak.org/demo/loc/usa/baltimore'),
(10, 'http://epcis.fosstrak.org/demo/loc/usa/newport'),
(11, 'http://epcis.fosstrak.org/demo/loc/germany/hamburg');

COMMIT;
