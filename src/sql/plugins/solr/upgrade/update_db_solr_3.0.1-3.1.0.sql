ALTER TABLE solr_fields ADD facet_mincount int DEFAULT 1;
ALTER TABLE solr_fields ADD operator_type  varchar(30) DEFAULT 'OR';
INSERT INTO solr_fields (name, label, description, weight, facet_mincount, operator_type) VALUES("title", "title" , "Title", 10, 1, "AND");
INSERT INTO solr_fields (name, label, description, weight, facet_mincount, operator_type) VALUES("content", "content" , "Content", 1, 1, "AND");
INSERT INTO solr_fields (name, label, description, weight, facet_mincount, operator_type) VALUES("summary", "summary" , "Summary", 1, 1, "AND");
