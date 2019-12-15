-- add the new column to insurance_type
alter table insurance_type add column insurance_type_shorthand text;

-- convert all existing data
update insurance_type set insurance_type_shorthand = 'BCBS' where insurance_type_id = 1;
update insurance_type set insurance_type_shorthand = 'MedCr' where insurance_type_id = 2;
update insurance_type set insurance_type_shorthand = 'TriCr' where insurance_type_id = 3;
update insurance_type set insurance_type_shorthand = 'UHC' where insurance_type_id = 4;
update insurance_type set insurance_type_shorthand = 'UHCUMR' where insurance_type_id = 5;
update insurance_type set insurance_type_shorthand = 'WrkCmp' where insurance_type_id = 6;
update insurance_type set insurance_type_shorthand = 'Cash' where insurance_type_id = 7;
update insurance_type set insurance_type_shorthand = 'Csh-DB' where insurance_type_id = 8;
update insurance_type set insurance_type_shorthand = 'Hmna' where insurance_type_id = 9;


-- add report table
CREATE TABLE report (
    report_id serial PRIMARY KEY,
    report_status text,
    report_date date,
    generated_date date
);


-- add report_metric table
CREATE TABLE report_metric (
    report_metric_id bigserial PRIMARY KEY,
    report_id integer,
    patient_type_id integer,
    insurance_type_id integer,
    therapist_id integer,					-- where Employee.role=therapist
    visit_type_id integer,
    count integer
);


grant all privileges on database postgres to ndowma;
grant all privileges on all sequences in schema public to ndowma;
grant select, insert, update, delete on all tables in schema public to ndowma;

grant all privileges on database postgres to aherrman;
grant all privileges on all sequences in schema public to aherrman;
grant select, insert, update, delete on all tables in schema public to aherrman;

grant all privileges on database postgres to sgalloway;
grant all privileges on all sequences in schema public to sgalloway;
grant select, insert, update, delete on all tables in schema public to sgalloway;

grant all privileges on database postgres to scurtis;
grant all privileges on all sequences in schema public to scurtis;
grant select, insert, update, delete on all tables in schema public to scurtis;

grant all privileges on database postgres to tneff;
grant all privileges on all sequences in schema public to tneff;
grant select, insert, update, delete on all tables in schema public to tneff;

grant all privileges on database postgres to ddowma;
grant all privileges on all sequences in schema public to ddowma;
grant select, insert, update, delete on all tables in schema public to ddowma;
