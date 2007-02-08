-- Copyright (c) 2007 David Gubler, Arthur van Dorp
-- All rights reserved.
-- 
-- For copying and distribution information, please see the file
-- LICENSE.
--
-- DB creation for mysql. If you make changes, please change 
-- EPCIS_Schema_Template first and other .sql-files accordingly.
--
-- For easy conversion from template, replace:
-- bigint CHECK("id">0) PRIMARY KEY => bigint PRIMARY KEY auto_increment
-- " => `
--
-- Also add or change:
-- SET storage_engine=INNODB;
-- Remove all UNIQUE constraints on varchar attributes which can be
-- longer than 767 bytes because mysql can't handle them.
--
-- CAVEATS: 
-- bigint(20) replaced by bigint, only difference is display length not specified.
-- datetime replaced by timestamp (SQL standard, any caveats?).
-- enum('ADD','OBSERVE','DELETE') replaced with
-- varchar(8) CHECK (action IN ('ADD','OBSERVE','DELETE'))
-- rechecked action type with new standard -> ok.
-- Vocabularies: All uris are now UNIQUE.
-- BizTransactions have now a defined type.
-- All identifiers are double quoted and therefore case sensitive:
-- Use double quotes in queries as well.
--
-- Mysql specific:
-- auto_increment

-- CREATE DB with UTF8

BEGIN;

SET storage_engine=INNODB;

INSERT INTO `vocabularies` (`uri`, `table_name`) VALUES
('urn:epcglobal:epcis:vtype:BusinessStep','voc_BizStep'),
('urn:epcglobal:epcis:vtype:BusinessTransaction','voc_BizTrans'),
('urn:epcglobal:epcis:vtype:Disposition','voc_Disposition'),
('urn:epcglobal:epcis:vtype:ReadPoint','voc_ReadPoint'),
('urn:epcglobal:epcis:vtype:BusinessLocation','voc_BizLoc')
;

INSERT INTO `voc_bizstep` (`id`, `uri`) VALUES 
('NULL', 'urn:epcglobal:hls:bizstep:commissioning'),
('NULL', 'urn:epcglobal:hls:bizstep:casetopalletaggregation'),
('NULL', 'urn:epcglobal:fmcg:bizstep:receiving'),
('NULL', 'urn:epcglobal:fmcg:bizstep:picking'),
('NULL', 'urn:epcglobal:fmcg:bizstep:shipping')
;

INSERT INTO `voc_bizloc` (`id`, `uri`) VALUES
('1', 'urn:epcglobal:fmcg:loc:0614141073467.1'),
('2', 'urn:epcglobal:fmcg:loc:0614141073467'),
('3', 'urn:epcglobal:fmcg:loc:0614141073468'),
('4', 'urn:epcglobal:fmcg:loc:0614141073468.1'),
('5', 'urn:epcglobal:fmcg:loc:0614141073468.2'),
('6', 'urn:epcglobal:fmcg:loc:0614141073468.3'),
('7', 'urn:epcglobal:fmcg:loc:0614141073469'),
('8', 'urn:epcglobal:fmcg:loc:0614141073469.1'),
('9', 'urn:epcglobal:fmcg:loc:0614141073469.2')
;

INSERT INTO `bizloc_attr` (`id`,`attribute`,`value`) VALUES
('1', 'fmcg:gln', '0614141073467'),
('1', 'fmcg:sslt','208'),
('1', 'fmcg:sslta','422'),
('2', 'fmcg:gln', '0614141073467'),
('3', 'fmcg:gln',  '0614141073468'),
('4', 'fmcg:gln', '0614141073468'),
('4', 'fmcg:sslt', '209'),
('5', 'fmcg:gln', '0614141073468'),
('5', 'fmcg:sslt', '299'),
('5', 'fmcg:sslta', '422'),
('6', 'fmcg:gln', '0614141073468'),
('6', 'fmcg:sslt', '210'),
('7', 'fmcg:gln', '0614141073469'),
('8', 'fmcg:gln', '0614141073469'),
('8', 'fmcg:sslt', '209'),
('9', 'fmcg:gln', '0614141073469'),
('9', 'fmcg:sslt', '201')
;



INSERT INTO `voc_readpoint` (`id`,`uri`) VALUES
('NULL', 'urn:epcglobal:fmcg:loc:0614141073467.RP-1'),
('NULL', 'urn:epcglobal:fmcg:loc:0614141073468.RP-1'),
('NULL', 'urn:epcglobal:fmcg:loc:0614141073468.RP-2'),
('NULL', 'urn:epcglobal:fmcg:loc:0614141073468.RP-3'),
('NULL', 'urn:epcglobal:fmcg:loc:0614141073469.RP-1')
;

INSERT INTO `readpoint_attr` (`id`,`attribute`,`value`) VALUES 
('1','fmcg:gln', '0614141073467'),
('2','fmcg:gln', '0614141073468'),
('3','fmcg:gln', '0614141073468'),
('4','fmcg:gln', '0614141073468'),
('5','fmcg:gln', '0614141073469'),
('1','fmcg:sslt','208'),
('2','fmcg:sslt','209'),
('3','fmcg:sslt','299'),
('4','fmcg:sslt','210'),
('5','fmcg:sslt', '209'),
('1','fmcg:sslta', '422'),
('2','fmcg:sslta', '414'),
('3','fmcg:sslta', '422'),
('4','fmcg:sslta', '414'),
('5','fmcg:sslta', '414'),
('1','fmcg:sle','PackagingLineX'),
('2','fmcg:sle','DockDoor3'),
('3','fmcg:sle','PickArea5'),
('4','fmcg:sle','DockDoor6'),
('5','fmcg:sle','DockDoor1')
;

INSERT INTO `voc_disposition` (`id`,`uri`) VALUES
('NULL', 'urn:epcglobal:fmcg:disp:sellable_available'),
('NULL', 'urn:epcglobal:hls:disp:active')
;


COMMIT;
