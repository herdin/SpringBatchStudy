-- Autogenerated: do not edit this file
select * from batch_job_instance where 1=1;
select * from batch_job_execution where 1=1;
select * from batch_job_execution_context where 1=1;
select * from batch_job_execution_params where 1=1;
select * from batch_step_execution where 1=1;
select * from batch_step_execution_context where 1=1;

select * from batch_job_instance where 1=1
and job_name = 'simple-job-with-add-execution-context-inc'
order by job_instance_id desc
;
select * from batch_job_execution_context where job_execution_id = 46
;
select * from batch_step_execution where 1=1
and job_execution_id = 46
;
select * from batch_step_execution_context where 1=1
and step_execution_id = 54
;
select * from batch_job_execution_params where 1=1
and job_execution_id = 46
;
select * from book