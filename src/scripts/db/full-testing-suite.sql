
--
-- this script populates the db with useful, consistent data to perform manual testing with.
--

-- diagnosis!
-- INSERT INTO diagnosis(diagnosis_id, diagnosis_code, diagnosis_name, diagnosis_type, diagnosis_type_order, display_order) VALUES (?, ?, ?, ?, ?, ?);
INSERT INTO diagnosis(diagnosis_id, diagnosis_code, diagnosis_name, diagnosis_type, diagnosis_type_order, display_order) VALUES (1, 'R51', 'headache/facial pain', 'Spine: Head', 1, 1);
INSERT INTO diagnosis(diagnosis_id, diagnosis_code, diagnosis_name, diagnosis_type, diagnosis_type_order, display_order) VALUES (2, 'G44.229', 'Tension HA: chronic', 'Spine: Head', 1, 2);
INSERT INTO diagnosis(diagnosis_id, diagnosis_code, diagnosis_name, diagnosis_type, diagnosis_type_order, display_order) VALUES (3, 'G44.219', 'Tension HA: episodic', 'Spine: Head', 1, 3);
INSERT INTO diagnosis(diagnosis_id, diagnosis_code, diagnosis_name, diagnosis_type, diagnosis_type_order, display_order) VALUES (4, 'G43.___', 'Migraine...', 'Spine: Head', 1, 4);
INSERT INTO diagnosis(diagnosis_id, diagnosis_code, diagnosis_name, diagnosis_type, diagnosis_type_order, display_order) VALUES (5, 'M54.2', 'Cervical pain', 'Spine: Cervical', 2, 1);
INSERT INTO diagnosis(diagnosis_id, diagnosis_code, diagnosis_name, diagnosis_type, diagnosis_type_order, display_order) VALUES (6, 'M50.0...', 'cervical disc...', 'Spine: Cervical', 2, 2);
INSERT INTO diagnosis(diagnosis_id, diagnosis_code, diagnosis_name, diagnosis_type, diagnosis_type_order, display_order) VALUES (7, 'M54.12', 'cervical radicluopathy', 'Spine: Cervical', 2, 3);
INSERT INTO diagnosis(diagnosis_id, diagnosis_code, diagnosis_name, diagnosis_type, diagnosis_type_order, display_order) VALUES (8, 'M99...', 'Cervical stenosis', 'Spine: Cervical', 2, 4);
INSERT INTO diagnosis(diagnosis_id, diagnosis_code, diagnosis_name, diagnosis_type, diagnosis_type_order, display_order) VALUES (9, 'M50.81/M50.82/M50.83', 'Cervical DDD', 'Spine: Cervical', 2, 5);
INSERT INTO diagnosis(diagnosis_id, diagnosis_code, diagnosis_name, diagnosis_type, diagnosis_type_order, display_order) VALUES (10, 'M54.6', 'thoracic pain', 'Spine: Thoracic', 3, 1);
INSERT INTO diagnosis(diagnosis_id, diagnosis_code, diagnosis_name, diagnosis_type, diagnosis_type_order, display_order) VALUES (11, 'M51.0...', 'thoracic disc...', 'Spine: Thoracic', 3, 2);
INSERT INTO diagnosis(diagnosis_id, diagnosis_code, diagnosis_name, diagnosis_type, diagnosis_type_order, display_order) VALUES (12, 'M54.14', 'thoracic radiculopathy', 'Spine: Thoracic', 3, 3);



-- insurance!
-- INSERT INTO insurance_type(insurance_type_id, insurance_type_name) VALUES (?, ?);
INSERT INTO insurance_type(insurance_type_id, insurance_type_name) VALUES (1, 'BCBS');
INSERT INTO insurance_type(insurance_type_id, insurance_type_name) VALUES (2, 'Medicare');
INSERT INTO insurance_type(insurance_type_id, insurance_type_name) VALUES (3, 'Cash');
INSERT INTO insurance_type(insurance_type_id, insurance_type_name) VALUES (4, 'Tricare');
INSERT INTO insurance_type(insurance_type_id, insurance_type_name) VALUES (5, 'UHC');
INSERT INTO insurance_type(insurance_type_id, insurance_type_name) VALUES (6, 'Work Comp');



-- patient_type
-- INSERT INTO patient_type(patient_type_id, patient_type_name) VALUES (?, ?);
INSERT INTO patient_type(patient_type_id, patient_type_name) VALUES (1, 'Orthopedic');
INSERT INTO patient_type(patient_type_id, patient_type_name) VALUES (2, 'Scoliosis');
INSERT INTO patient_type(patient_type_id, patient_type_name) VALUES (3, 'Dancer');
INSERT INTO patient_type(patient_type_id, patient_type_name) VALUES (4, 'POTS');
INSERT INTO patient_type(patient_type_id, patient_type_name) VALUES (5, 'Client');



-- treatment
-- INSERT INTO treatment(treatment_id, treatment_code, treatment_name, display_order) VALUES (?, ?, ?, ?);
INSERT INTO treatment(treatment_id, treatment_code, treatment_name, display_order) VALUES (1, '97161', 'Eval low complex', 1);
INSERT INTO treatment(treatment_id, treatment_code, treatment_name, display_order) VALUES (2, '97162', 'Eval mod complex', 2);
INSERT INTO treatment(treatment_id, treatment_code, treatment_name, display_order) VALUES (3, '97163', 'Eval high complex', 3);
INSERT INTO treatment(treatment_id, treatment_code, treatment_name, display_order) VALUES (4, '97112', 'Neuromuscular Re-Education', 4);
INSERT INTO treatment(treatment_id, treatment_code, treatment_name, display_order) VALUES (5, '97110', 'Therapeutic Exercise', 5);
INSERT INTO treatment(treatment_id, treatment_code, treatment_name, display_order) VALUES (6, '97140', 'Manual Therapy', 6);
INSERT INTO treatment(treatment_id, treatment_code, treatment_name, display_order) VALUES (7, '97530', 'Therapeutic Activities', 7);
INSERT INTO treatment(treatment_id, treatment_code, treatment_name, display_order) VALUES (8, '97164', 'Re-evaluation', 8);
INSERT INTO treatment(treatment_id, treatment_code, treatment_name, display_order) VALUES (9, '97035', 'Ultrasound', 9);
INSERT INTO treatment(treatment_id, treatment_code, treatment_name, display_order) VALUES (10, '97799', 'Dry Needling', 10);




-- visit_type
-- INSERT INTO visit_type(visit_type_id, visit_type_name) VALUES (?, ?);
INSERT INTO visit_type(visit_type_id, visit_type_name) VALUES (1, 'Initial');
INSERT INTO visit_type(visit_type_id, visit_type_name) VALUES (2, 'Follow up');
INSERT INTO visit_type(visit_type_id, visit_type_name) VALUES (3, 'Cancel/No Show');



-- patients!
-- INSERT INTO patient(patient_id, first_name, last_name, patient_type_id, insurance_type_id, notes) VALUES (?, ?, ?, ?, ?, ?);
INSERT INTO patient(patient_id, first_name, last_name, patient_type_id, insurance_type_id, notes) VALUES (1000001, 'Olga', 'Hammerstein', 5, 5, 'Pain in the ass');
INSERT INTO patient(patient_id, first_name, last_name, patient_type_id, insurance_type_id, notes) VALUES (1000002, 'Pickles', 'Wimbergot', 4, 4, 'Loves cucumbers');
INSERT INTO patient(patient_id, first_name, last_name, patient_type_id, insurance_type_id, notes) VALUES (1000003, 'Sally', 'Smith', 2, 1, 'Wears the best shoes');
INSERT INTO patient(patient_id, first_name, last_name, patient_type_id, insurance_type_id, notes) VALUES (1000004, 'Arnold', 'Drummond', 1, 2, '80s icon');
INSERT INTO patient(patient_id, first_name, last_name, patient_type_id, insurance_type_id, notes) VALUES (1000005, 'Mario', 'Chitagatzi', 5, 3, 'Soooo italian');




-- patient-diagnosis
-- INSERT INTO patient_diagnosis(patient_diagnosis_id, patient_id, diagnosis_id) VALUES (?, ?, ?);
INSERT INTO patient_diagnosis(patient_diagnosis_id, patient_id, diagnosis_id) VALUES (1000001, 1000001, 1);
INSERT INTO patient_diagnosis(patient_diagnosis_id, patient_id, diagnosis_id) VALUES (1000002, 1000001, 3);
INSERT INTO patient_diagnosis(patient_diagnosis_id, patient_id, diagnosis_id) VALUES (1000003, 1000001, 5);
INSERT INTO patient_diagnosis(patient_diagnosis_id, patient_id, diagnosis_id) VALUES (1000004, 1000002, 2);
INSERT INTO patient_diagnosis(patient_diagnosis_id, patient_id, diagnosis_id) VALUES (1000005, 1000002, 10);
INSERT INTO patient_diagnosis(patient_diagnosis_id, patient_id, diagnosis_id) VALUES (1000006, 1000003, 5);
INSERT INTO patient_diagnosis(patient_diagnosis_id, patient_id, diagnosis_id) VALUES (1000007, 1000003, 7);
INSERT INTO patient_diagnosis(patient_diagnosis_id, patient_id, diagnosis_id) VALUES (1000008, 1000003, 9);
INSERT INTO patient_diagnosis(patient_diagnosis_id, patient_id, diagnosis_id) VALUES (1000009, 1000003, 11);
INSERT INTO patient_diagnosis(patient_diagnosis_id, patient_id, diagnosis_id) VALUES (1000010, 1000004, 4);
INSERT INTO patient_diagnosis(patient_diagnosis_id, patient_id, diagnosis_id) VALUES (1000011, 1000005, 1);
INSERT INTO patient_diagnosis(patient_diagnosis_id, patient_id, diagnosis_id) VALUES (1000012, 1000005, 3);
INSERT INTO patient_diagnosis(patient_diagnosis_id, patient_id, diagnosis_id) VALUES (1000013, 1000005, 6);
INSERT INTO patient_diagnosis(patient_diagnosis_id, patient_id, diagnosis_id) VALUES (1000014, 1000005, 10);



