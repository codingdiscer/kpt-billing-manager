-- add new column to metrics to track treatment count
alter table report_metric add column treatment_count integer default 0;

-- the delete statement to undo the column (for when necessary)
--alter table report_metric drop column treatment_count;
