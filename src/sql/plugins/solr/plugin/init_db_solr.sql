--
-- Dumping data for table solr_facet
--
INSERT INTO solr_fields (id_field,name,label,description,is_facet,enable_facet,is_sort,enable_sort,default_sort) VALUES 
(1,'site','Site','Site',true,true,false,false,false);

INSERT INTO solr_fields (id_field,name,label,description,is_facet,enable_facet,is_sort,enable_sort,default_sort) VALUES 
(2,'type','Type','Type',true,true,false,false,false);

INSERT INTO solr_fields (id_field,name,label,description,is_facet,enable_facet,is_sort,enable_sort,default_sort) VALUES 
(3,'date','Date','Date',true,true,true,true,false);

INSERT INTO solr_fields (id_field,name,label,description,is_facet,enable_facet,is_sort,enable_sort,default_sort) VALUES 
(4,'score','Score','Score',false,false,true,true,true);

INSERT INTO solr_fields (id_field,name,label,description,is_facet,enable_facet,is_sort,enable_sort,default_sort) VALUES 
(5,'categorie','Categorie','Category',true,true,false,false,false);

INSERT INTO solr_fields (id_field, name, label, description, weight, facet_mincount, operator_type) VALUES
(6, 'title', 'title', 'Title', 10, 1, 'AND');

INSERT INTO solr_fields (id_field, name, label, description, weight, facet_mincount, operator_type) VALUES
(7, 'content', 'content', 'Content', 0.1, 1, 'AND');

INSERT INTO solr_fields (id_field, name, label, description, weight, facet_mincount, operator_type) VALUES
(8, 'summary', 'summary', 'Summary', 1, 1, 'AND');
