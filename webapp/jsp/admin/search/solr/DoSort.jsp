<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:useBean id="solr_conf" scope="session" class="fr.paris.lutece.plugins.search.solr.web.SolrConfigurationJspBean" />

<% solr_conf.init( request , solr_conf.RIGHT_CONFIGURATION ); %>
<%  response.sendRedirect(solr_conf.doSort( request )); %>
