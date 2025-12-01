<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%@page import="fr.paris.lutece.plugins.search.solr.web.SolrIndexerJspBean"%>

${ solrIndexerJspBean.init( pageContext.request , SolrIndexerJspBean.RIGHT_INDEXER ) }
${ solrIndexerJspBean.getIndexingProperties( pageContext.request ) }

<%@ include file="../../AdminFooter.jsp" %>
