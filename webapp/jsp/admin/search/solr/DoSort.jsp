<%@ page errorPage="../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.search.solr.web.SolrConfigurationJspBean"%>

${ solrConfigurationJspBean.init( pageContext.request, SolrConfigurationJspBean.RIGHT_CONFIGURATION ) }
${ pageContext.response.sendRedirect(solrConfigurationJspBean.doSort( pageContext.request ) ) }
