/*==============================================================*/
/* Update :  core_admin_right 								    */
/*==============================================================*/
INSERT INTO core_admin_right(id_right, name, level_right, admin_url, description, is_updatable, plugin_name, id_feature_group, icon_url, documentation_url, id_order) VALUES ('SOLR_INDEX_MANAGEMENT', 'search.solr.adminFeature.title', 1, 'jsp/admin/search/solr/ManageSearchIndexation.jsp', 'search.solr.adminFeature.description', 0, 'solr', 'SYSTEM', 'ti ti-cloud-bolt', NULL, NULL);
INSERT INTO core_admin_right(id_right, name, level_right, admin_url, description, is_updatable, plugin_name, id_feature_group, icon_url, documentation_url, id_order) VALUES ('SOLR_CONFIGURATION_MANAGEMENT', 'search.solr.adminFeature.configuration.title', 1, 'jsp/admin/search/solr/ManageSearchConfiguration.jsp', 'search.solr.adminFeature.configuration.description', 0, 'solr', 'SYSTEM', 'ti ti-settings-search', NULL, NULL);
INSERT INTO core_admin_right(id_right, name, level_right, admin_url, description, is_updatable, plugin_name, id_feature_group, icon_url, documentation_url, id_order) VALUES ('SOLR_FIELDS_MANAGEMENT', 'search.solr.adminFeature.fields.title', 1, 'jsp/admin/search/solr/ManageSolrFields.jsp', 'search.solr.adminFeature.fields.description', 0, 'solr', 'SYSTEM', 'ti ti-pencil-search', NULL, NULL);
INSERT INTO core_user_right VALUES ('SOLR_INDEX_MANAGEMENT',1);
INSERT INTO core_user_right VALUES ('SOLR_INDEX_MANAGEMENT',2);
INSERT INTO core_user_right VALUES ('SOLR_CONFIGURATION_MANAGEMENT',2);
INSERT INTO core_user_right VALUES ('SOLR_FIELDS_MANAGEMENT',1);
INSERT INTO core_user_right VALUES ('SOLR_FIELDS_MANAGEMENT',2);