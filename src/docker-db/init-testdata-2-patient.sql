

--
-- this script populates the db with useful, consistent data to perform manual testing with.
--

-- patients!
-- INSERT INTO patient(patient_id, first_name, last_name, patient_type_id, insurance_type_id, notes) VALUES (?, ?, ?, ?, ?, ?);
INSERT INTO patient(patient_id, first_name, last_name, patient_type_id, insurance_type_id, notes) VALUES (1000001, 'Olga', 'Hammerstein', 5, 5, 'Pain in the ass');
INSERT INTO patient(patient_id, first_name, last_name, patient_type_id, insurance_type_id, notes) VALUES (1000002, 'Pickles', 'Wimbergot', 4, 4, 'Loves cucumbers');
INSERT INTO patient(patient_id, first_name, last_name, patient_type_id, insurance_type_id, notes) VALUES (1000003, 'Sally', 'Smith', 2, 1, 'Wears the best shoes');
INSERT INTO patient(patient_id, first_name, last_name, patient_type_id, insurance_type_id, notes) VALUES (1000004, 'Arnold', 'Drummond', 1, 2, '80s icon');
INSERT INTO patient(patient_id, first_name, last_name, patient_type_id, insurance_type_id, notes) VALUES (1000005, 'Mario', 'Chitagatzi', 5, 3, 'Soooo italian');


	


