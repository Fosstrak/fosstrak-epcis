-- Ooops, these files have been forgotten and are possibly outdated.
-- Deletes all master data (vocabularies).

BEGIN;

---- Vocabularies ----

DELETE FROM voc_BizLoc;
DELETE FROM voc_BizStep;
DELETE FROM voc_BizTransType;
DELETE FROM voc_BizTrans;
DELETE FROM voc_Disposition;
DELETE FROM voc_ReadPoint;
DELETE FROM voc_EPCClass;

COMMIT;

