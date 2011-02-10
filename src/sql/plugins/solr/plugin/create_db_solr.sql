--
-- Table structure for table solr_facet
--
DROP TABLE IF EXISTS solr_fields;
CREATE TABLE solr_fields (
	id_field int default NULL,
	name varchar(30) default NULL,
	label varchar(30) default NULL,
	description varchar(255) default NULL,
	type varchar(30) default NULL,
	is_facet boolean default false,
	enable_facet boolean default false,
	is_sort boolean default false,
	enable_sort boolean default false,
	default_sort boolean default false,
	PRIMARY KEY (id_field)
);

DROP TABLE IF EXISTS solr_facet_intersection;
CREATE TABLE solr_facet_intersection (
	id_field1 int default NULL,
	id_field2 int default NULL
);