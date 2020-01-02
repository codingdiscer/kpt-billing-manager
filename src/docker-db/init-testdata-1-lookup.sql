
--
-- this script populates the db with useful, consistent data to perform manual testing with.
--


-- diagnosis_type!
-- INSERT INTO diagnosis_type(diagnosis_type_id, diagnosis_type_name, display_order) VALUES (?, ?, ?);

-- diagnosis!
-- INSERT INTO diagnosis(diagnosis_id, diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (?, ?, ?, ?, ?);

INSERT INTO diagnosis_type(diagnosis_type_id, diagnosis_type_name, display_order) VALUES (1, 'Spine: Head', 1);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (1, 'R51', 'Headache/facial pain', 1);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (1, 'G44.229', 'Tension HA: chronic', 2);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (1, 'G44.219', 'Tension HA: episodic', 3);
INSERT INTO diagnosis_type(diagnosis_type_id, diagnosis_type_name, display_order) VALUES (2, 'Spine: Cervical', 2);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (2, 'M54.2', 'Cervical pain', 1);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (2, 'M54.12', 'Cervical radicluopathy', 2);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (2, 'M48.02', 'Cervical stenosis', 3);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (2, 'M50.81', 'Cervical DDD', 4);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (2, 'M50.820', 'Other Cervical Disc Disorder C4-C5', 5);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (2, 'M50.822', 'Other Cervical Disc C5-C6', 6);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (2, 'M50.823', 'Other Cervical Disc C6-C7', 7);
INSERT INTO diagnosis_type(diagnosis_type_id, diagnosis_type_name, display_order) VALUES (3, 'Spine: Thoracic', 3);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (3, 'M54.6', 'Thoracic pain', 1);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (3, 'M51.04', 'Invtervertebral disc disorder w/ myelopathy thoracic region', 2);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (3, 'M51.05', 'Invtervertebral disc disorder w/ myelopathy thoracolumbar region', 3);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (3, 'M51.06', 'Invtervertebral disc disorders with myelopthy, lumbar region', 4);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (3, 'M54.14', 'Thoracic radiculopathy', 5);
INSERT INTO diagnosis_type(diagnosis_type_id, diagnosis_type_name, display_order) VALUES (4, 'Spine: Lumbar', 4);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (4, 'M54.5', 'Low back pain', 1);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (4, 'M54.41', 'Low back pain with Sciatica R', 2);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (4, 'M54.42', 'Low back pain with Sciatica L', 3);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (4, 'M54.16', 'Lumbar radiculopathy', 4);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (4, 'M54.18', 'Lumbosacral radiculopathy', 5);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (4, 'M48.01', 'Spinal stenosis, occipito-atlanto-axial region', 6);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (4, 'M48.02', 'Spinal stenosis, cervical region', 7);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (4, 'M48.03', 'Spinal stenosis, cervicothoracic region', 8);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (4, 'M48.04', 'Spinal stenosis, thoracic region', 9);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (4, 'M48.05', 'Spinal stenosis, thoracolumbar region', 10);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (4, 'M48.061', 'Spinal stenosis, lumbar region w/o neurogenic claudication', 11);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (4, 'M48.062', 'Spinal stenosis, lumbar region with neurogenic claudication', 12);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (4, 'M48.07', 'Spinal stenosis, lumbosacral region', 13);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (4, 'M48.08', 'Spinal stenosis, sacral and sacrococcygeal region', 14);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (4, 'M51.34', 'Other intervertebral disc degeneration, thoracic region', 15);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (4, 'M51.36', 'Other intervertebral disc degeneration, thoracolumbar region', 16);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (4, 'M51.37', 'Other intervertebral disc degeneration, lumbosacral region', 17);
INSERT INTO diagnosis_type(diagnosis_type_id, diagnosis_type_name, display_order) VALUES (5, 'Spine: Sacral', 5);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (5, 'M46.1', 'Sacroilitis', 1);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (5, 'M53.2x7', 'Lumbosacral instability', 2);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (5, 'M41.122', 'Scoliosis: Adolescent: AIS: c/s', 3);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (5, 'M41.123', 'Scoliosis: Adolescent AIS: C-T', 4);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (5, 'M41.124', 'Scoliosis: Adolescent AIS: t/s', 5);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (5, 'M41.125', 'Scoliosis: Adolescent AIS: T-L', 6);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (5, 'M41.126', 'Scoliosis: Adolescent AIS: l/s', 7);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (5, 'M41.127', 'Scoliosis: AdolescentAIS: L-S', 8);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (5, 'M41.112', 'Scoliosis: Juevenile JIS: c/s', 9);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (5, 'M41.113', 'Scoliosis: Juevenile JIS: C-T ', 10);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (5, 'M41.114', 'Scoliosis: Juevenile JIS: t/s', 11);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (5, 'M41.115', 'Scoliosis: Juevenile JIS: T-L', 12);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (5, 'M41.116', 'Scoliosis: Juevenile JIS: L/s', 13);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (5, 'M41.117', 'Scoliosis: Juevenile JIS: L-S', 14);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (5, 'M41.25', 'Scoliosis: Other idiopathic: T-L', 15);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (5, 'M41.85', 'Scoliosis: Other T-L', 16);
INSERT INTO diagnosis_type(diagnosis_type_id, diagnosis_type_name, display_order) VALUES (7, 'Posture', 7);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (7, 'M40.03', 'Kyphosis:C-T', 1);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (7, 'M40.04', 'Kyphosis:t/s', 2);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (7, 'M40.05', 'Kyphosis: T-L', 3);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (7, 'R29.3', 'Abnormal Posture', 4);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (7, 'M40.45', 'Lordosis: T-L (postural)', 5);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (7, 'M40.46', 'Lordosis: lumbar (postural)', 6);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (7, 'M40.57', 'Lordosis: lumbosacral (postural)', 7);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (7, 'M40.55', 'Lordosis: unsp, thoracolumbar region', 8);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (7, 'M40.56', 'Lordosis: unsp, lumbar region', 9);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (7, 'M40.57', 'Lordosis: unsp, lumbosacral region', 10);
INSERT INTO diagnosis_type(diagnosis_type_id, diagnosis_type_name, display_order) VALUES (8, 'Shoulder', 8);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (8, 'M25.511', 'Shoulder pain Right', 1);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (8, 'M25.552', 'Shoulder pain Left', 2);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (8, 'M25.611', 'Shoulder stiffness Right', 3);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (8, 'M25.612', 'Shoulder stiffness Left', 4);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (8, 'M75.41', 'Shoulder impingement Right', 5);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (8, 'M75.42', 'Shoulder impingement Left', 6);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (8, 'M75.01', 'Adhesive capsulitis of shoulder Right', 7);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (8, 'M75.02', 'Adhesive capsulitis of shoulder Left', 8);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (8, 'M79.621', 'Upper arm pain (axillary) Right', 9);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (8, 'M79.622', 'Upper arm pain (axillary) Left', 10);
INSERT INTO diagnosis_type(diagnosis_type_id, diagnosis_type_name, display_order) VALUES (9, 'Elbow', 9);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (9, 'M25.521', 'Elbow pain Right', 1);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (9, 'M25.522', 'Elbow pain Left', 2);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (9, 'M25.621', 'Elbow stiffness Right', 3);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (9, 'M25.622', 'Elbow stiffness Left', 4);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (9, 'M79.631', 'Forearm pain Right', 5);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (9, 'M79.632', 'Forearm pain Left', 6);
INSERT INTO diagnosis_type(diagnosis_type_id, diagnosis_type_name, display_order) VALUES (10, 'Hand', 10);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (10, 'M79.641', 'Hand pain Right', 1);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (10, 'M79.642', 'Hand pain Left', 2);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (10, 'M79.644', 'Finger pain', 3);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (10, 'M79.645', 'Finger pain', 4);
INSERT INTO diagnosis_type(diagnosis_type_id, diagnosis_type_name, display_order) VALUES (11, 'Hip', 11);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (11, 'M25.551', 'Hip pain Right', 1);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (11, 'M25.552', 'Hip pain Left', 2);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (11, 'M25.651', 'Hip stiffness Right', 3);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (11, 'M25.652', 'Hip Stiffness Left', 4);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (11, 'M79.651', 'Thigh pain Right', 5);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (11, 'M79.652', 'Thigh Pain Left', 6);
INSERT INTO diagnosis_type(diagnosis_type_id, diagnosis_type_name, display_order) VALUES (12, 'Knee', 12);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (12, 'M25.561', 'Knee pain Right', 1);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (12, 'M25.562', 'Knee Pain Left', 2);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (12, 'M25.661', 'Knee stiffness Right', 3);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (12, 'M25.662', 'Knee stiffness Left', 4);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (12, 'M22.2x1', 'Patellofemoral syndrome Right', 5);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (12, 'M22.2x2', 'Patellofemoral Syndrome Left', 6);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (12, 'M79.661', 'Lower leg pain Right', 7);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (12, 'M79.662', 'Lower leg pain Left', 8);
INSERT INTO diagnosis_type(diagnosis_type_id, diagnosis_type_name, display_order) VALUES (13, 'Ankle', 13);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (13, 'M25.571', 'Ankle pain Right', 1);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (13, 'M25.572', 'Ankle pain Left', 2);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (13, 'M25.671', 'Ankle stiffness Right', 3);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (13, 'M25.672', 'Ankle stiffness Left', 4);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (13, 'M76.61', 'Achilles Tendinitis Right', 5);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (13, 'M76.62', 'Achilles Tendinitis Left', 6);
INSERT INTO diagnosis_type(diagnosis_type_id, diagnosis_type_name, display_order) VALUES (14, 'Foot', 14);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (14, 'M26.674', 'Foot stiffness: Right', 1);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (14, 'M26.675', 'Foot stiffness: Left', 2);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (14, 'M72.2', 'Plantarfascitis', 3);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (14, 'M79.671', 'Foot pain Right', 4);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (14, 'M79.672', 'Foot Pain Left', 5);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (14, 'M79.674', 'Toe pain Right', 6);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (14, 'M79.675', 'Toe pain Left', 7);
INSERT INTO diagnosis_type(diagnosis_type_id, diagnosis_type_name, display_order) VALUES (15, 'Misc', 15);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (15, 'R26.2', 'Difficulty walking', 1);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (15, 'R53.1', 'Generalized weakness/Asthenia', 2);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (15, 'M62.81', 'Muscle weakness', 3);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (15, 'M53.81', 'Malaise/debility/deterioration', 4);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (15, 'R53.83', 'Fatigue', 5);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (15, 'Q79.6', 'Ehlers Danlos Syndrome', 6);
INSERT INTO diagnosis_type(diagnosis_type_id, diagnosis_type_name, display_order) VALUES (16, 'Dysautonomia/POTS', 16);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (16, 'G90.9', 'Dysautonomia', 1);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (16, 'I95.1', 'Orthostatic hypotension', 2);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (16, 'R55', 'Syncope', 3);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (16, 'R42', 'Postural dizziness with presyncope', 4);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (16, 'R00.0', 'POTS', 5);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (16, 'I49.8', 'POTS', 6);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (16, 'F07.81', 'Post concusion syndrome', 7);
INSERT INTO diagnosis_type(diagnosis_type_id, diagnosis_type_name, display_order) VALUES (17, 'Neuro', 17);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (17, 'G20', 'Parkinsons', 1);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (17, 'I63.9', 'Unspecified CVA', 2);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (17, 'Z91.81', 'History of Falls', 3);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (17, 'R29.6', 'Repeated Falls', 4);
INSERT INTO diagnosis_type(diagnosis_type_id, diagnosis_type_name, display_order) VALUES (18, 'Vertigo', 18);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (18, 'R42', 'Vertigo', 1);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (18, 'H81.11', 'Positional Vertigo Right Ear', 2);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (18, 'H81.12', 'Positional Vertigo Left Ear', 3);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (18, 'H81.13', 'Bilat', 4);
INSERT INTO diagnosis_type(diagnosis_type_id, diagnosis_type_name, display_order) VALUES (19, 'Joint Replacement', 19);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (19, 'Z47.1', 'After Care Joint replacement', 1);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (19, 'Z96.611', 'Shoulder joint Right', 2);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (19, 'Z96.612', 'Shoulder joint Left', 3);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (19, 'Z96.641', 'Hip Joint Right', 4);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (19, 'Z96.642', 'Hip Joint Left', 5);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (19, 'Z96.643', 'Hip Joint Bilateral', 6);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (19, 'Z96.651', 'Knee Joint Right', 7);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (19, 'Z96.652', 'Knee Joint Left', 8);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (19, 'Z96.653', 'Knee Joint Bilateral', 9);
INSERT INTO diagnosis_type(diagnosis_type_id, diagnosis_type_name, display_order) VALUES (20, 'Unknown', 20);
INSERT INTO diagnosis( diagnosis_type_id, diagnosis_code, diagnosis_name, display_order) VALUES (20, 'UNKN', 'Unknown', 1);





-- insurance!
-- INSERT INTO insurance_type(insurance_type_id, insurance_type_name) VALUES (?, ?);
INSERT INTO insurance_type(insurance_type_id, insurance_type_name, insurance_type_shorthand) VALUES (1, 'BCBS', 		'BCBS');
INSERT INTO insurance_type(insurance_type_id, insurance_type_name, insurance_type_shorthand) VALUES (2, 'Medicare', 	'MedCr');
INSERT INTO insurance_type(insurance_type_id, insurance_type_name, insurance_type_shorthand) VALUES (3, 'Tricare', 		'TriCr');
INSERT INTO insurance_type(insurance_type_id, insurance_type_name, insurance_type_shorthand) VALUES (4, 'UHC', 			'UHC');
INSERT INTO insurance_type(insurance_type_id, insurance_type_name, insurance_type_shorthand) VALUES (5, 'UHC-UMR', 		'UHCUMR');
INSERT INTO insurance_type(insurance_type_id, insurance_type_name, insurance_type_shorthand) VALUES (6, 'Work Comp',	'WrkCmp');
INSERT INTO insurance_type(insurance_type_id, insurance_type_name, insurance_type_shorthand) VALUES (7, 'Cash', 		'Cash');
INSERT INTO insurance_type(insurance_type_id, insurance_type_name, insurance_type_shorthand) VALUES (8, 'Cash-Don''t bill', 'Csh-DB');
INSERT INTO insurance_type(insurance_type_id, insurance_type_name, insurance_type_shorthand) VALUES (9, 'Humana',		'Hmna');


-- patient_type
-- INSERT INTO patient_type(patient_type_id, patient_type_name) VALUES (?, ?);
INSERT INTO patient_type(patient_type_id, patient_type_name) VALUES (1, 'Orthopedic');
INSERT INTO patient_type(patient_type_id, patient_type_name) VALUES (2, 'Scoliosis');
INSERT INTO patient_type(patient_type_id, patient_type_name) VALUES (3, 'Dancer');
INSERT INTO patient_type(patient_type_id, patient_type_name) VALUES (4, 'POTS');
INSERT INTO patient_type(patient_type_id, patient_type_name) VALUES (5, 'Neuro');
INSERT INTO patient_type(patient_type_id, patient_type_name) VALUES (6, 'Client');
INSERT INTO patient_type(patient_type_id, patient_type_name) VALUES (7, 'Oral');
INSERT INTO patient_type(patient_type_id, patient_type_name) VALUES (8, 'Hypermobility');


-- treatment
-- INSERT INTO treatment(treatment_id, treatment_code, treatment_name, display_order) VALUES (?, ?, ?, ?, ?);
INSERT INTO treatment(treatment_id, treatment_code, treatment_name, display_order, is_evaluation) VALUES (1, '97112', 'Neuromuscular Re-Education', 1, false);
INSERT INTO treatment(treatment_id, treatment_code, treatment_name, display_order, is_evaluation) VALUES (2, '97110', 'Therapeutic Exercise', 2, false);
INSERT INTO treatment(treatment_id, treatment_code, treatment_name, display_order, is_evaluation) VALUES (3, '97140', 'Manual Therapy', 3, false);
INSERT INTO treatment(treatment_id, treatment_code, treatment_name, display_order, is_evaluation) VALUES (4, '97530', 'Therapeutic Activities', 4, false);
INSERT INTO treatment(treatment_id, treatment_code, treatment_name, display_order, is_evaluation) VALUES (5, '97116', 'Gait Training', 5, false);
INSERT INTO treatment(treatment_id, treatment_code, treatment_name, display_order, is_evaluation) VALUES (6, '97035', 'Ultrasound', 6, false);
INSERT INTO treatment(treatment_id, treatment_code, treatment_name, display_order, is_evaluation) VALUES (7, '20560', 'Dry Needling 1 or 2 muscles', 7, false);
INSERT INTO treatment(treatment_id, treatment_code, treatment_name, display_order, is_evaluation) VALUES (8, '97161', 'Eval low complex', 9, true);
INSERT INTO treatment(treatment_id, treatment_code, treatment_name, display_order, is_evaluation) VALUES (9, '97162', 'Eval mod complex', 10, true);
INSERT INTO treatment(treatment_id, treatment_code, treatment_name, display_order, is_evaluation) VALUES (10, '97163', 'Eval high complex', 11, true);
INSERT INTO treatment(treatment_id, treatment_code, treatment_name, display_order, is_evaluation) VALUES (11, '97164', 'Re-evaluation', 13, true);
INSERT INTO treatment(treatment_id, treatment_code, treatment_name, display_order, is_evaluation) VALUES (12, '20561', 'Wheelchair Assessment', 12, true);
INSERT INTO treatment(treatment_id, treatment_code, treatment_name, display_order, is_evaluation) VALUES (13, '20561', 'Dry Needling 3 or more muscles', 8, false);


-- fixing the old dry needling number
UPDATE treatment set (treatment_id, treatment_code, treatment_name, display_order, is_evaluation) VALUES (7, '97799', 'Dry Needling', 7, false);


INSERT INTO treatment(treatment_id, treatment_code, treatment_name, display_order, is_evaluation) VALUES (7, '20560', 'Dry Needling 1 or 2 muscles', 7, false);
INSERT INTO treatment(treatment_id, treatment_code, treatment_name, display_order, is_evaluation) VALUES (8, '97161', 'Eval low complex', 9, true);
INSERT INTO treatment(treatment_id, treatment_code, treatment_name, display_order, is_evaluation) VALUES (9, '97162', 'Eval mod complex', 10, true);
INSERT INTO treatment(treatment_id, treatment_code, treatment_name, display_order, is_evaluation) VALUES (10, '97163', 'Eval high complex', 11, true);
INSERT INTO treatment(treatment_id, treatment_code, treatment_name, display_order, is_evaluation) VALUES (11, '97164', 'Re-evaluation', 12, true);
INSERT INTO treatment(treatment_id, treatment_code, treatment_name, display_order, is_evaluation) VALUES (12, '20561', 'Dry Needling 3 or more muscles', 8, true);



-- visit_type
-- INSERT INTO visit_type(visit_type_id, visit_type_name) VALUES (?, ?);
INSERT INTO visit_type(visit_type_id, visit_type_name) VALUES (1, 'Initial');
INSERT INTO visit_type(visit_type_id, visit_type_name) VALUES (2, 'Follow up');
INSERT INTO visit_type(visit_type_id, visit_type_name) VALUES (3, 'Cancel/No Show');




