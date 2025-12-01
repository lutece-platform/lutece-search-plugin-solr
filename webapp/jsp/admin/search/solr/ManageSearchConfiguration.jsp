<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%@page import="fr.paris.lutece.plugins.search.solr.web.SolrConfigurationJspBean"%>

${ solrConfigurationJspBean.init( pageContext.request , SolrConfigurationJspBean.RIGHT_CONFIGURATION ) }

${ solrConfigurationJspBean.getPage( pageContext.request ) }

<%@ include file="../../AdminFooter.jsp" %>
