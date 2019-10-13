

-- employees!
--insert into employee (employee_id, username, fullname) values (?, ?, ?);
insert into employee (employee_id, username, fullname) values (1, 'ndowma', 'Noelle Dowma');
insert into employee (employee_id, username, fullname) values (2, 'aherrman', 'Ashley Herrman');
insert into employee (employee_id, username, fullname) values (3, 'sgalloway', 'Stacy Galloway');
insert into employee (employee_id, username, fullname) values (4, 'scurtis', 'Stacey Curtis-Yeager');
insert into employee (employee_id, username, fullname) values (5, 'tneff', 'Tamara Neff');
insert into employee (employee_id, username, fullname) values (6, 'brisenhoover', 'Bree Risenhoover');
insert into employee (employee_id, username, fullname) values (343, 'ddowma', 'Dan Dowma');


-- employee_roles!!
--INSERT INTO employee_roles(employee_roles_id, employee_id, employee_role_id) VALUES (?, ?, ?);
INSERT INTO employee_roles(employee_roles_id, employee_id, employee_role) VALUES (10, 1, 'ADMINISTRATOR');
INSERT INTO employee_roles(employee_roles_id, employee_id, employee_role) VALUES (11, 1, 'THERAPIST');
INSERT INTO employee_roles(employee_roles_id, employee_id, employee_role) VALUES (12, 1, 'SCHEDULER');
INSERT INTO employee_roles(employee_roles_id, employee_id, employee_role) VALUES (13, 1, 'INSURANCE_BILLER');
INSERT INTO employee_roles(employee_roles_id, employee_id, employee_role) VALUES (14, 1, 'DIAGNOSIS_CODER');
INSERT INTO employee_roles(employee_roles_id, employee_id, employee_role) VALUES (15, 1, 'PATIENTS');

INSERT INTO employee_roles(employee_roles_id, employee_id, employee_role) VALUES (20, 2, 'THERAPIST');
INSERT INTO employee_roles(employee_roles_id, employee_id, employee_role) VALUES (21, 2, 'PATIENTS');
INSERT INTO employee_roles(employee_roles_id, employee_id, employee_role) VALUES (22, 2, 'SCHEDULER');

INSERT INTO employee_roles(employee_roles_id, employee_id, employee_role) VALUES (30, 3, 'SCHEDULER');
INSERT INTO employee_roles(employee_roles_id, employee_id, employee_role) VALUES (31, 3, 'INSURANCE_BILLER');
INSERT INTO employee_roles(employee_roles_id, employee_id, employee_role) VALUES (32, 3, 'DIAGNOSIS_CODER');
INSERT INTO employee_roles(employee_roles_id, employee_id, employee_role) VALUES (33, 3, 'PATIENTS');

INSERT INTO employee_roles(employee_roles_id, employee_id, employee_role) VALUES (40, 4, 'THERAPIST');
INSERT INTO employee_roles(employee_roles_id, employee_id, employee_role) VALUES (41, 4, 'PATIENTS');
INSERT INTO employee_roles(employee_roles_id, employee_id, employee_role) VALUES (42, 4, 'SCHEDULER');

INSERT INTO employee_roles(employee_roles_id, employee_id, employee_role) VALUES (50, 5, 'THERAPIST');
INSERT INTO employee_roles(employee_roles_id, employee_id, employee_role) VALUES (51, 5, 'PATIENTS');
INSERT INTO employee_roles(employee_roles_id, employee_id, employee_role) VALUES (52, 5, 'SCHEDULER');

INSERT INTO employee_roles(employee_roles_id, employee_id, employee_role) VALUES (60, 6, 'SCHEDULER');
INSERT INTO employee_roles(employee_roles_id, employee_id, employee_role) VALUES (61, 6, 'INSURANCE_BILLER');
INSERT INTO employee_roles(employee_roles_id, employee_id, employee_role) VALUES (62, 6, 'PATIENTS');

INSERT INTO employee_roles(employee_roles_id, employee_id, employee_role) VALUES (100, 343, 'SUPER_ADMIN');


--SUPER_ADMIN=0, ADMINISTRATOR=1
--THERAPIST=2, SCHEDULER=3, INSURANCE_BILLER=4
--DIAGNOSIS_CODER=5, PATIENTS=6
