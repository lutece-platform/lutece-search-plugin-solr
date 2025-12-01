<%@ page errorPage="../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.search.solr.web.SolrIndexerJspBean"%>

${ solrIndexerJspBean.init( pageContext.request, SolrIndexerJspBean.RIGHT_INDEXER ) }
${ solrIndexerJspBean.doIndexing( pageContext.request ) }
${ pageContext.response.sendRedirect( SolrIndexerJspBean.JSP_VIEW_INDEXATION ) }