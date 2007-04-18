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
--
--
-- Ooops, these files have been forgotten and are possibly outdated.
-- Deletes all master data (vocabularies).

BEGIN;

---- Vocabularies ----

DELETE FROM voc_BizLoc;
DELETE FROM BizLoc_attr;
DELETE FROM voc_BizStep;
DELETE FROM voc_BizTransType;
DELETE FROM voc_BizTrans;
DELETE FROM voc_Disposition;
DELETE FROM voc_ReadPoint;
DELETE FROM ReadPoint_attr;
DELETE FROM voc_EPCClass;

COMMIT;

