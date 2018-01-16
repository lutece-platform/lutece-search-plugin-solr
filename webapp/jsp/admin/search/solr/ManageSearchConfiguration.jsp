<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<jsp:useBean id="solr_conf" scope="session" class="fr.paris.lutece.plugins.search.solr.web.SolrConfigurationJspBean" />

<% solr_conf.init( request , solr_conf.RIGHT_CONFIGURATION ); %>

<%= solr_conf.getPage( request ) %>

<%@ include file="../../AdminFooter.jsp" %>
