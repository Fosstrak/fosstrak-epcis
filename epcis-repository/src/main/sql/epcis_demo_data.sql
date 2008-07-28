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
(1, '2006-06-01 15:55:04', '2006-08-18 13:50:07', '+01:00', 'urn:x:bar:5:036544:007325', 'ADD', 2, 2, 3, 1);

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
(1, '2006-05-10 04:50:35', '2006-08-18 13:50:11', '+01:00', 'OBSERVE', 3, 3, 4, 3),
(2, '2006-05-09 21:01:44', '2006-08-18 13:50:16', '+01:00', 'OBSERVE', 4, 4, 5, 4),
(3, '2006-04-03 22:36:17', '2006-08-18 13:50:21', '+01:00', 'ADD', 5, 1, 6, 5),
(4, '2006-04-03 20:33:31', '2006-08-18 13:50:26', '+01:00', 'OBSERVE', 5, NULL, 7, 5),
(5, '2006-05-10 21:38:46', '2006-09-01 00:05:37', '+01:00', 'ADD', 5, NULL, 8, 6),
(6, '2006-05-21 09:21:40', '2006-09-01 00:06:03', '+01:00', 'OBSERVE', 3, NULL, 9, 7),
(7, '2006-06-27 16:55:08', '2006-09-01 00:06:16', '+01:00', 'OBSERVE', 3, NULL, 10, 8),
(8, '2006-06-16 14:28:23', '2006-09-01 00:07:46', '+01:00', 'ADD', 5, NULL, 8, 6),
(9, '2006-08-13 17:07:50', '2006-09-01 00:09:37', '+01:00', 'OBSERVE', 3, NULL, 11, 9),
(10, '2006-09-15 11:45:06', '2006-09-01 00:10:12', '+01:00', 'OBSERVE', 3, NULL, 10, 8),
(11, '2007-04-23 12:26:52', '2007-04-23 12:26:52', '+01:00', 'OBSERVE', 6, NULL, 5, 4);

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
(2, '2006-01-15 16:15:31', '2006-08-18 13:50:00', '+01:00', 2, 67, 1, 1, 2, 2);


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
(1, 'urn:epcglobal:fmcg:ssl:0037000.00729.450'),
(2, 'urn:epcglobal:fmcg:ssl:0066000.00102.007'),
(3, 'urn:epcglobal:fmcg:ssl:0037000.00811.217'),
(4, 'urn:epcglobal:fmcg:ssl:0037000.00729.215'),
(5, 'urn:epcglobal:fmcg:ssl:0037000.00729.210'),
(6, 'urn:demo:loc:morristown'),
(7, 'urn:demo:loc:baltimore'),
(8, 'urn:demo:loc:newport'),
(9, 'urn:demo:loc:singapore');

INSERT INTO `voc_BizLoc_attr` (`id`, `attribute`, `value`) VALUES
(3, 'urn:epcglobal:fmcg:foo', 'Room5'),
(4, 'urn:epcglobal:fmcg:bar', 'Door3'),
(6, 'urn:demo:st', 'NJ'),
(6, 'urn:demo:ctry', 'US');

INSERT INTO `voc_BizStep` (`id`, `uri`) VALUES
(1, 'urn:epcglobal:epcis:bizstep:fmcg:physinv'),
(2, 'urn:epcglobal:epcis:bizstep:fmcg:pickandpack'),
(3, 'urn:epcglobal:epcis:bizstep:fmcg:received'),
(4, 'urn:epcglobal:epcis:bizstep:fmcg:loading'),
(5, 'urn:epcglobal:epcis:bizstep:fmcg:production'),
(6, 'urn:epcglobal:epcis:bizstep:fmcg:shipping');

INSERT INTO `voc_BizTrans` (`id`, `uri`) VALUES
(1, 'http://transaction.acme.com/tracker/6677150'),
(2, 'http://transaction.acme.com/po/12345678'),
(3, 'http://transaction.acme.com/asn/1152');

INSERT INTO `voc_BizTransType` (`id`, `uri`) VALUES
(1, 'urn:epcglobal:fmcg:btt:po'),
(2, 'urn:epcglobal:fmcg:btt:asn');

INSERT INTO `voc_Disposition` (`id`, `uri`) VALUES
(1, 'urn:epcglobal:epcis:disp:fmcg:readyforuse'),
(2, 'urn:epcglobal:epcis:disp:fmcg:readyforpickup'),
(3, 'urn:epcglobal:epcis:disp:fmcg:inrepair'),
(4, 'urn:epcglobal:epcis:disp:fmcg:transit');

INSERT INTO `voc_EPCClass` (`id`, `uri`) VALUES
(1, 'urn:epc:id:sgtin:0069000.919923'),
(2, 'urn:epc:id:sgtin:0069000.957110');

INSERT INTO `voc_ReadPoint` (`id`, `uri`) VALUES
(1, 'urn:epcglobal:fmcg:ssl:0037000.00729.451,2'),
(2, 'urn:epcglobal:fmcg:ssl:0066000.00102.014,001'),
(3, 'urn:epcglobal:fmcg:ssl:0037000.00729.450,9'),
(4, 'urn:epcglobal:fmcg:ssl:0037000.00811.217,058'),
(5, 'urn:epcglobal:fmcg:ssl:0037000.00729.215,803'),
(6, 'urn:epcglobal:fmcg:ssl:0037000.00729.210,432'),
(7, 'urn:epcglobal:fmcg:ssl:0037000.00729.210,414'),
(8, 'urn:demo:loc:morristown'),
(9, 'urn:demo:loc:baltimore'),
(10, 'urn:demo:loc:newport'),
(11, 'urn:demo:loc:singapore');

INSERT INTO `voc_ReadPoint_attr` (`id`, `attribute`, `value`) VALUES
(1, 'urn:epcglobal:fmcg:mda:gln', '0614141073467'),
(1, 'urn:epcglobal:fmcg:mda:sslt', '208'),
(1, 'urn:epcglobal:fmcg:mda:sslta', '422'),
(1, 'urn:epcglobal:fmcg:mda:sle', 'PackagingLineX'),
(2, 'urn:epcglobal:fmcg:mda:gln', '0614141073468'),
(2, 'urn:epcglobal:fmcg:mda:sslt', '209'),
(2, 'urn:epcglobal:fmcg:mda:sslta', '414'),
(2, 'urn:epcglobal:fmcg:mda:sle', 'DockDoor3');

COMMIT;
