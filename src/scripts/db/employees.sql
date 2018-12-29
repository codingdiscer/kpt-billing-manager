

-- employees!
--insert into employee (employee_id, username, password, fullname) values (?, ?, ?, ?);
insert into employee (employee_id, username, password, fullname) values (1, 'noelle.dowma', '', 'Noelle Dowma');
insert into employee (employee_id, username, password, fullname) values (2, 'ashley.herrman', '', 'Ashley Herrman');
insert into employee (employee_id, username, password, fullname) values (3, 'stacy.galloway', '', 'Stacy Galloway');
insert into employee (employee_id, username, password, fullname) values (4, 'stacey.curtis', '', 'Stacey Curtis-Yeager');
insert into employee (employee_id, username, password, fullname) values (343, 'dan.dowma', 'goofy_smile', 'Dan Dowma');


-- employee_roles!!
--INSERT INTO employee_roles(employee_roles_id, employee_id, employee_role_id) VALUES (?, ?, ?);
INSERT INTO employee_roles(employee_roles_id, employee_id, employee_role_id) VALUES (1, 1, 1);
INSERT INTO employee_roles(employee_roles_id, employee_id, employee_role_id) VALUES (2, 1, 2);

INSERT INTO employee_roles(employee_roles_id, employee_id, employee_role_id) VALUES (3, 2, 2);

INSERT INTO employee_roles(employee_roles_id, employee_id, employee_role_id) VALUES (4, 3, 3);
INSERT INTO employee_roles(employee_roles_id, employee_id, employee_role_id) VALUES (5, 3, 4);

INSERT INTO employee_roles(employee_roles_id, employee_id, employee_role_id) VALUES (6, 343, 0);
