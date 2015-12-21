ALTER TABLE solr_fields ADD facet_mincount int DEFAULT 1;
ALTER TABLE solr_fields ADD operator_type  varchar(30) DEFAULT 'OR';

SELECT @max := MAX( id_field ) FROM solr_fields;
INSERT INTO solr_fields (id_field, name, label, description, weight, facet_mincount, operator_type) VALUES(@max + 1, "title", "title", "Title", 10, 1, "AND");
INSERT INTO solr_fields (id_field, name, label, description, weight, facet_mincount, operator_type) VALUES(@max + 2, "content", "content", "Content", 1, 1, "AND");
INSERT INTO solr_fields (id_field, name, label, description, weight, facet_mincount, operator_type) VALUES(@max + 3, "summary", "summary", "Summary", 1, 1, "AND");
