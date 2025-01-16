ALTER TABLE datasource_error_log
    ADD stack_trace TEXT;
ALTER TABLE datasource_error_log
    ADD message TEXT;
ALTER TABLE datasource_error_log
    ADD method_signature TEXT;