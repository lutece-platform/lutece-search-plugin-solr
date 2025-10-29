-- liquibase formatted sql
-- changeset solr:update_db_solr_4.0.6-4.0.7.sql
-- preconditions onFail:MARK_RAN onError:WARN
ALTER TABLE solr_fields modify COLUMN id_field int AUTO_INCREMENT;
ALTER TABLE solr_indexer_action modify COLUMN id_action int AUTO_INCREMENT;