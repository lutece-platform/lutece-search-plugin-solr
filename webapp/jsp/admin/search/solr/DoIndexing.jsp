<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:useBean id="solr_index" scope="session" class="fr.paris.lutece.plugins.search.solr.web.SolrIndexerJspBean" />

<%
    solr_index.init( request , solr_index.RIGHT_INDEXER );
	response.sendRedirect( solr_index.doIndexing( request ) );
%>
