-- liquibase formatted sql
-- changeset solr:update_db_solr_4.0.2-4.0.6.sql
-- preconditions onFail:MARK_RAN onError:WARN
UPDATE core_admin_right SET icon_url='ti ti-pencil-search' WHERE id_right='SOLR_FIELDS_MANAGEMENT';
UPDATE core_admin_right SET icon_url='ti ti-settings-search' WHERE id_right='SOLR_CONFIGURATION_MANAGEMENT';
UPDATE core_admin_right SET icon_url='ti ti-cloud-bolt' WHERE id_right='SOLR_INDEX_MANAGEMENT';