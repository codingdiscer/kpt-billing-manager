#!/bin/bash
set -e

# POSTGRES_USER, POSTGRES_PASSWORD are set in docker/Dockerfile

psql -v ON_ERROR_STOP=1 -v --username "$POSTGRES_USER" <<-EOSQL
	CREATE TABLE employee (
		employee_id serial PRIMARY KEY,
		username text,
		fullname text
	);
	
	CREATE TABLE employee_login (
		employee_login_id bigserial PRIMARY KEY,
		employee_id integer,
		login_time timestamp with time zone
	);
  
  
	CREATE TABLE employee_roles (
		employee_roles_id serial PRIMARY KEY,
		employee_id integer,
		employee_role text				-- from the EmployeeRole enum
	);
  
	
	CREATE TABLE patient (
		patient_id bigserial PRIMARY KEY,
		first_name text,
		last_name text,
		patient_type_id integer,
		insurance_type_id integer,
		notes text
	);

	
	CREATE TABLE patient_type (
		patient_type_id serial PRIMARY KEY,
		patient_type_name text
	
	);
	
	CREATE TABLE insurance_type (
		insurance_type_id serial PRIMARY KEY,
		insurance_type_name text,
		insurance_type_shorthand text
	);
	
	CREATE TABLE visit (
		visit_id bigserial PRIMARY KEY,
		visit_date date,
		patient_id bigserial,
		patient_type_id integer,
		insurance_type_id integer,
		therapist_id integer,					-- where Employee.role=therapist
		visit_type_id integer,
		visit_status_id integer,				-- from the VisitStatus enum
		visit_status text,						-- from the VisitStatus enum
		notes text,
		visit_number integer
	);
	
	CREATE TABLE visit_status_change (
		visit_status_change_id bigserial PRIMARY KEY,
		visit_status_id integer,				-- from the VisitStatus enum
		visit_status text,						-- from the VisitStatus enum
		visit_id bigserial,
		changed timestamp,
		employee_id integer
	);
	
	
	CREATE TABLE diagnosis_type (
		diagnosis_type_id serial PRIMARY KEY,
		diagnosis_type_name text,
		display_order integer
	);

	CREATE TABLE diagnosis (
		diagnosis_id serial PRIMARY KEY,
		diagnosis_type_id integer,
		diagnosis_code text,
		diagnosis_name text,
		display_order integer
	);

	CREATE TABLE visit_diagnosis (
		visit_diagnosis_id bigserial PRIMARY KEY,
		visit_id bigserial,
		diagnosis_id integer
	);

	CREATE TABLE treatment (
		treatment_id serial PRIMARY KEY,
		treatment_code text, 
		treatment_name text,
		display_order integer,
		is_evaluation boolean 
	);

	CREATE TABLE visit_treatment (
		visit_treatment_id bigserial PRIMARY KEY,
		visit_id bigserial,
		treatment_id integer,
		treatment_quantity integer
	);

	CREATE TABLE visit_type (
		visit_type_id serial PRIMARY KEY,
		visit_type_name text
	);

	CREATE TABLE report (
		report_id serial PRIMARY KEY,
		report_status text,
		report_date date,
		generated_date date
	);

	CREATE TABLE report_metric (
		report_metric_id bigserial PRIMARY KEY,
		report_id integer,		
		patient_type_id integer,
		insurance_type_id integer,
		therapist_id integer,					-- where Employee.role=therapist
		visit_type_id integer,
		count integer
	);
	
	
	create user ndowma with encrypted password 'pass';
	grant all privileges on database postgres to ndowma;
	grant all privileges on all sequences in schema public to ndowma;
	grant select, insert, update, delete on all tables in schema public to ndowma;

	create user aherrman with encrypted password 'pass';
	grant all privileges on database postgres to aherrman;
	grant all privileges on all sequences in schema public to aherrman;
	grant select, insert, update, delete on all tables in schema public to aherrman;
	
	create user sgalloway with encrypted password 'pass';
	grant all privileges on database postgres to sgalloway;
	grant all privileges on all sequences in schema public to sgalloway;
	grant select, insert, update, delete on all tables in schema public to sgalloway;

	create user scurtis with encrypted password 'pass';
	grant all privileges on database postgres to scurtis;
	grant all privileges on all sequences in schema public to scurtis;
	grant select, insert, update, delete on all tables in schema public to scurtis;

	create user tneff with encrypted password 'pass';
	grant all privileges on database postgres to tneff;
	grant all privileges on all sequences in schema public to tneff;
	grant select, insert, update, delete on all tables in schema public to tneff;

	create user ddowma with encrypted password 'pass';
	grant all privileges on database postgres to ddowma;
	grant all privileges on all sequences in schema public to ddowma;
	grant select, insert, update, delete on all tables in schema public to ddowma;
	
	
EOSQL


