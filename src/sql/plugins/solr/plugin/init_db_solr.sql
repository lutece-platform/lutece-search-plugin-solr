--
-- Dumping data for table solr_facet
--
INSERT INTO solr_fields (id_field,name,label,description,is_facet,enable_facet,is_sort,enable_sort,default_sort) VALUES 
(1,'site','Site','Le site d''appartenance du document ou page',true,true,false,false,false);

INSERT INTO solr_fields (id_field,name,label,description,is_facet,enable_facet,is_sort,enable_sort,default_sort) VALUES 
(2,'type','Type','Le type du document',true,true,false,false,false);

INSERT INTO solr_fields (id_field,name,label,description,is_facet,enable_facet,is_sort,enable_sort,default_sort) VALUES 
(3,'date','Date','La date de creation du document',true,true,true,true,false);

INSERT INTO solr_fields (id_field,name,label,description,is_facet,enable_facet,is_sort,enable_sort,default_sort) VALUES 
(4,'score','Score','Pertinence du résultat',false,false,true,true,true);

INSERT INTO solr_fields (id_field,name,label,description,is_facet,enable_facet,is_sort,enable_sort,default_sort) VALUES 
(5,'categorie','Categorie','La categorie du document',true,true,false,false,false);
				