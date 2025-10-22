-- liquibase formatted sql
-- changeset solr:update_db_solr_4.0.1-4.0.2.sql
-- preconditions onFail:MARK_RAN onError:WARN
ALTER TABLE `solr_fields` CHANGE COLUMN `name` `name` VARCHAR(75) NULL DEFAULT NULL COLLATE 'utf8_unicode_ci' AFTER `id_field`;