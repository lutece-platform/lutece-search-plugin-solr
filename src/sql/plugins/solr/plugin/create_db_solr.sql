--
-- Table structure for table solr_facet
--
DROP TABLE IF EXISTS solr_fields;
CREATE TABLE solr_fields (
	id_field int default NULL,
	name varchar(30) default NULL,
	label varchar(255) default NULL,
	description varchar(255) default NULL,
	is_facet boolean default false,
	enable_facet boolean default false,
	is_sort boolean default false,
	enable_sort boolean default false,
	default_sort boolean default false,
    weight FLOAT DEFAULT 0,
    facet_mincount INT DEFAULT 1,
    operator_type VARCHAR(30) DEFAULT 'OR',
    facet_order int DEFAULT 0,
	PRIMARY KEY (id_field)
);

DROP TABLE IF EXISTS solr_facet_intersection;
CREATE TABLE solr_facet_intersection (
	id_field1 int default NULL,
	id_field2 int default NULL
);

DROP TABLE IF EXISTS solr_indexer_action;
CREATE TABLE  solr_indexer_action (
  id_action int default 0 NOT NULL,
  id_document varchar(255) NOT NULL,
  id_task int default 0 NOT NULL,
  type_ressource varchar(255) NOT NULL,
  id_portlet int default 0 NOT NULL,
  PRIMARY KEY (id_action)
);
