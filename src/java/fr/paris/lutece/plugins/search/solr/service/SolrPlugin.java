package fr.paris.lutece.plugins.search.solr.service;

import fr.paris.lutece.plugins.search.solr.business.indexeraction.SolrIndexerAction;
import fr.paris.lutece.plugins.search.solr.business.indexeraction.SolrIndexerActionHome;
import fr.paris.lutece.portal.business.event.EventRessourceListener;
import fr.paris.lutece.portal.business.event.ResourceEvent;
import fr.paris.lutece.portal.business.indexeraction.IndexerAction;
import fr.paris.lutece.portal.service.event.ResourceEventManager;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginDefaultImplementation;
import fr.paris.lutece.portal.service.plugin.PluginService;

public class SolrPlugin extends PluginDefaultImplementation
{
	public static final String PLUGIN_NAME = "solr";
	public static final Plugin _plugin = PluginService.getPlugin( PLUGIN_NAME );
	
	@Override
	public void init()
	{
		super.init();
		
		// Subscribes to the EventManager
		ResourceEventManager.register( new EventRessourceListener()
		{
			public void updatedResource( ResourceEvent event )
			{
				SolrIndexerAction indexerAction = new SolrIndexerAction();
				indexerAction.setIdTask( IndexerAction.TASK_MODIFY );
				indexerAction.setIdDocument( event.getIdResource() );
				indexerAction.setIdPortlet( event.getIdPortlet() );
				indexerAction.setTypeResource( event.getTypeResource() );
				SolrIndexerActionHome.create( indexerAction, _plugin );
				
			}
			
			public void deletedResource( ResourceEvent event )
			{
				SolrIndexerAction indexerAction = new SolrIndexerAction();
				indexerAction.setIdTask( IndexerAction.TASK_DELETE );
				indexerAction.setIdDocument( event.getIdResource() );
				indexerAction.setIdPortlet( event.getIdPortlet() );
				indexerAction.setTypeResource( event.getTypeResource() );
				SolrIndexerActionHome.create( indexerAction, _plugin );
				
			}
			
			public void addedResource( ResourceEvent event )
			{
				SolrIndexerAction indexerAction = new SolrIndexerAction();
				indexerAction.setIdTask( IndexerAction.TASK_CREATE );
				indexerAction.setIdDocument( event.getIdResource() );
				indexerAction.setIdPortlet( event.getIdPortlet() );
				indexerAction.setTypeResource( event.getTypeResource() );
				SolrIndexerActionHome.create( indexerAction, _plugin );
			}

			public String getName()
			{
				return SolrPlugin.PLUGIN_NAME;
			}
		});
	}
}
