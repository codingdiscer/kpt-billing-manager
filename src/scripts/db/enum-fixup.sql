-- add the new column to employee_roles
alter table employee_roles add column employee_role text;

-- convert all existing data
update employee_roles set employee_role = 'SUPER_ADMIN' where employee_role_id = 0;
update employee_roles set employee_role = 'ADMINISTRATOR' where employee_role_id = 1;
update employee_roles set employee_role = 'THERAPIST' where employee_role_id = 2;
update employee_roles set employee_role = 'SCHEDULER' where employee_role_id = 3;
update employee_roles set employee_role = 'INSURANCE_BILLER' where employee_role_id = 4;
update employee_roles set employee_role = 'DIAGNOSIS_CODER' where employee_role_id = 5;
update employee_roles set employee_role = 'PATIENTS' where employee_role_id = 6;

-- remove the old column
--alter table employee_roles drop column employee_role_id;



-- add the new column to visit
alter table visit add column visit_status text;

-- convert all existing data
update visit set visit_status = 'VISIT_CREATED' where visit_status_id = 0;
update visit set visit_status = 'SEEN_BY_THERAPIST' where visit_status_id = 1;
update visit set visit_status = 'PREPARED_FOR_BILLING' where visit_status_id = 2;
update visit set visit_status = 'BILLED_TO_INSURANCE' where visit_status_id = 3;
update visit set visit_status = 'REMITTANCE_ENTERED' where visit_status_id = 4;
update visit set visit_status = 'AWAITING_SECONDARY' where visit_status_id = 5;
update visit set visit_status = 'BILL_SENT_TO_PATIENT' where visit_status_id = 6;
update visit set visit_status = 'PAID_IN_FULL' where visit_status_id = 7;

-- remove the old column
--alter table visit drop column visit_status_id;

