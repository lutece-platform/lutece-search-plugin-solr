-- liquibase formatted sql
-- changeset solr:update_db_solr_2.1.0-2.1.1.sql
-- preconditions onFail:MARK_RAN onError:WARN
ALTER TABLE solr_fields DROP COLUMN type;

CREATE TABLE  solr_indexer_action (
  id_action int default 0 NOT NULL,
  id_document varchar(255) NOT NULL,
  id_task int default 0 NOT NULL,
  type_ressource varchar(255) NOT NULL,
  id_portlet int default 0 NOT NULL,
  PRIMARY KEY (id_action)
);

ALTER TABLE solr_fields MODIFY label varchar(255) default NULL;