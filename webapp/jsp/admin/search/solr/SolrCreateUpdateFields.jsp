<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%@page import="fr.paris.lutece.plugins.search.solr.web.SolrFieldsManagementJspBean"%>

${ solrFieldsManagementJspBean.init( pageContext.request , SolrFieldsManagementJspBean.SOLR_FIELDS_MANAGEMENT ) }

${ solrFieldsManagementJspBean.getForm( pageContext.request ) }

<%@ include file="../../AdminFooter.jsp" %>
