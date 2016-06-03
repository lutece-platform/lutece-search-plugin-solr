<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<jsp:useBean id="solr_index" scope="session" class="fr.paris.lutece.plugins.search.solr.web.SolrIndexerJspBean" />

<% solr_index.init( request , solr_index.RIGHT_INDEXER ); %>
<%= solr_index.getIndexing( request ) %>

<%@ include file="../../AdminFooter.jsp" %>
