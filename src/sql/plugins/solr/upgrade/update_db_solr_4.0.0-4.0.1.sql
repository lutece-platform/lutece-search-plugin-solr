-- liquibase formatted sql
-- changeset solr:update_db_solr_4.0.0-4.0.1.sql
-- preconditions onFail:MARK_RAN onError:WARN
ALTER TABLE solr_fields MODIFY COLUMN id_field int NOT NULL;