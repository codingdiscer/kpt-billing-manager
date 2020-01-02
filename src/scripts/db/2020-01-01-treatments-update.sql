-- add a new dry needling treatment
INSERT INTO treatment(treatment_id, treatment_code, treatment_name, display_order, is_evaluation) VALUES (13, '20561', 'Dry Needling 3 or more muscles', 8, false);

-- fix the old dry needling number
UPDATE treatment set treatment_code = '20560', treatment_name = 'Dry Needling 1 or 2 muscles' where treatment_id = 7;

-- re-order all the treatments lower in the order
UPDATE treatment set display_order = 9 where treatment_id = 8;
UPDATE treatment set display_order = 10 where treatment_id = 9;
UPDATE treatment set display_order = 11 where treatment_id = 10;
UPDATE treatment set display_order = 13 where treatment_id = 11;
UPDATE treatment set display_order = 12 where treatment_id = 12;




