<!--%@ page errorPage="../../ErrorPage.jsp" %-->
<jsp:include page="../../AdminHeader.jsp" />

<jsp:useBean id="solr_fields" scope="session" class="fr.paris.lutece.plugins.search.solr.web.SolrFieldsManagementJspBean" />

<% solr_fields.init( request , solr_fields.SOLR_FIELDS_MANAGEMENT ); %>

<%  solr_fields.doFields( request ); %>

<%= solr_fields.getPage( request ) %>

<%@ include file="../../AdminFooter.jsp" %>
