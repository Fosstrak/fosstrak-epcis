BEGIN;

INSERT INTO `vocabularies` (`uri`, `table_name`) VALUES
('urn:epcglobal:epcis:vtype:BusinessStep','BizStep'),
('urn:epcglobal:epcis:vtype:BusinessTransaction','BizTrans'),
('urn:epcglobal:epcis:vtype:Disposition','Disposition'),
('urn:epcglobal:epcis:vtype:ReadPoint','ReadPoint'),
('urn:epcglobal:epcis:vtype:BusinessLocation','BizLoc');

INSERT INTO `voc_bizstep` (`id`, `uri`) VALUES 
('NULL', 'urn:epcglobal:hls:bizstep:commissioning'),
('NULL', 'urn:epcglobal:hls:bizstep:casetopalletaggregation'),
('NULL', 'urn:epcglobal:fmcg:bizstep:receiving'),
('NULL', 'urn:epcglobal:fmcg:bizstep:picking'),
('NULL', 'urn:epcglobal:fmcg:bizstep:shipping');

INSERT INTO `voc_bizloc` (`id`, `uri`) VALUES
('1', 'urn:epcglobal:fmcg:loc:0614141073467.1'),
('2', 'urn:epcglobal:fmcg:loc:0614141073467'),
('3', 'urn:epcglobal:fmcg:loc:0614141073468'),
('4', 'urn:epcglobal:fmcg:loc:0614141073468.1'),
('5', 'urn:epcglobal:fmcg:loc:0614141073468.2'),
('6', 'urn:epcglobal:fmcg:loc:0614141073468.3'),
('7', 'urn:epcglobal:fmcg:loc:0614141073469'),
('8', 'urn:epcglobal:fmcg:loc:0614141073469.1'),
('9', 'urn:epcglobal:fmcg:loc:0614141073469.2');

INSERT INTO `voc_bizloc_attr` (`id`,`attribute`,`value`) VALUES
('2', 'urn:epcglobal:fmcg:mda:gln', '0614141073467'),
('1', 'urn:epcglobal:fmcg:mda:sslt','208'),
('1', 'urn:epcglobal:fmcg:mda:sslta','422'),
('2', 'urn:epcglobal:fmcg:mda:gln', '0614141073467'),
('3', 'urn:epcglobal:fmcg:mda:gln',  '0614141073468'),
('4', 'urn:epcglobal:fmcg:mda:gln', '0614141073468'),
('4', 'urn:epcglobal:fmcg:mda:sslt', '209'),
('5', 'urn:epcglobal:fmcg:mda:gln', '0614141073468'),
('5', 'urn:epcglobal:fmcg:mda:sslt', '299'),
('5', 'urn:epcglobal:fmcg:mda:sslta', '422'),
('6', 'urn:epcglobal:fmcg:mda:gln', '0614141073468'),
('6', 'urn:epcglobal:fmcg:mda:sslt', '210'),
('7', 'urn:epcglobal:fmcg:mda:gln', '0614141073469'),
('8', 'urn:epcglobal:fmcg:mda:gln', '0614141073469'),
('8', 'urn:epcglobal:fmcg:mda:sslt', '209'),
('9', 'urn:epcglobal:fmcg:mda:gln', '0614141073469'),
('9', 'urn:epcglobal:fmcg:mda:sslt', '201');

INSERT INTO `voc_readpoint` (`id`,`uri`) VALUES
('NULL', 'urn:epcglobal:fmcg:loc:0614141073467.RP-1'),
('NULL', 'urn:epcglobal:fmcg:loc:0614141073468.RP-1'),
('NULL', 'urn:epcglobal:fmcg:loc:0614141073468.RP-2'),
('NULL', 'urn:epcglobal:fmcg:loc:0614141073468.RP-3'),
('NULL', 'urn:epcglobal:fmcg:loc:0614141073469.RP-1');

INSERT INTO `voc_readpoint_attr` (`id`,`attribute`,`value`) VALUES 
('1','urn:epcglobal:fmcg:mda:gln', '0614141073467'),
('2','urn:epcglobal:fmcg:mda:gln', '0614141073468'),
('3','urn:epcglobal:fmcg:mda:gln', '0614141073468'),
('4','urn:epcglobal:fmcg:mda:gln', '0614141073468'),
('5','urn:epcglobal:fmcg:mda:gln', '0614141073469'),
('1','urn:epcglobal:fmcg:mda:sslt','208'),
('2','urn:epcglobal:fmcg:mda:sslt','209'),
('3','urn:epcglobal:fmcg:mda:sslt','299'),
('4','urn:epcglobal:fmcg:mda:sslt','210'),
('5','urn:epcglobal:fmcg:mda:sslt', '209'),
('1','urn:epcglobal:fmcg:mda:sslta', '422'),
('2','urn:epcglobal:fmcg:mda:sslta', '414'),
('3','urn:epcglobal:fmcg:mda:sslta', '422'),
('4','urn:epcglobal:fmcg:mda:sslta', '414'),
('5','urn:epcglobal:fmcg:mda:sslta', '414'),
('1','urn:epcglobal:fmcg:mda:sle','PackagingLineX'),
('2','urn:epcglobal:fmcg:mda:sle','DockDoor3'),
('3','urn:epcglobal:fmcg:mda:sle','PickArea5'),
('4','urn:epcglobal:fmcg:mda:sle','DockDoor6'),
('5','urn:epcglobal:fmcg:mda:sle','DockDoor1');

INSERT INTO `voc_disposition` (`id`,`uri`) VALUES
('NULL', 'urn:epcglobal:fmcg:disp:sellable_available'),
('NULL', 'urn:epcglobal:hls:disp:active');

COMMIT;
