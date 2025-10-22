-- liquibase formatted sql
-- changeset solr:update_db_solr_3.0.0-3.0.1.sql
-- preconditions onFail:MARK_RAN onError:WARN
ALTER TABLE solr_fields ADD weight float DEFAULT 1;
