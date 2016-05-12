package com.agroknow.cimmyt.controllers;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.HasParentQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermFilterBuilder;
import org.elasticsearch.search.SearchHit;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import org.springframework.web.bind.annotation.RequestMethod;


@RestController
public class IDController {

	@ApiOperation(value = "Get entity by Id", nickname = "find entity values by id")
    @RequestMapping(method = RequestMethod.GET, path="/entity/{id}"/*, produces = {"application/json","application/xml"}*/)
	@ApiImplicitParams({
        @ApiImplicitParam(
        			name = "id", 
        			value = "entity's id", 
        			required = true, 
        			dataType = "string", 
        			paramType = "path", 
        			defaultValue="10883_1009")
      })	
	String run(@PathVariable String id) {
        
    	Settings settings = ImmutableSettings.settingsBuilder()
		        .put("cluster.name", "agroknow").build();
    	
    	Client client = new TransportClient(settings)
		        .addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
		        //.addTransportAddress(new InetSocketTransportAddress("host2", 9300));
		System.out.println("Status:"+client.settings().toString());
		// on shutdown
		
		GetResponse response = client.prepareGet("cimmyt", "object", id)
		        .execute()
		        .actionGet();
		
		BoolQueryBuilder build_o =QueryBuilders.boolQuery();
		build_o.must(QueryBuilders.termQuery("object.appid", id));
		HasParentQueryBuilder qb=QueryBuilders.hasParentQuery("object",
					QueryBuilders.matchQuery("appid", id));

		SearchResponse responseSpecific=client
				.prepareSearch("cimmyt")
				.setQuery(qb)
				//.setSize(1)
				.execute()
				.actionGet();
		//System.out.println(qb.toString());
		
		/*GetResponse responseSpecific = client
				.prepareGet("cimmyt", 
					response.getSourceAsMap().get("type").toString()
					, id)
		        .execute()
		        .actionGet();*/
    	/*
		
		TermFilterBuilder termFilter = FilterBuilders.termFilter("location.value", "Mexico");
	    HasParentQueryBuilder object_query = 
	    		QueryBuilders.hasParentQuery("object", 
	    				QueryBuilders.matchQuery("title.value", "Evaluation"));
	    
	    SearchResponse searchResponse3 = 
	    		client.prepareSearch("cimmyt")
	    		.setQuery(QueryBuilders.filteredQuery(
	    				object_query, termFilter))
	    		.setSize(1)
	    		.execute()
	    		.actionGet();
		*/
		
		client.close();
		
		String results="";
		
		int size=0;
		if(!response.getSourceAsString().isEmpty())
			size=1;
		
		
		
		String specific_source="";
		
		for(SearchHit hit : responseSpecific.getHits().getHits())
		{
			specific_source=hit.getSourceAsString();
			break;
		}
		
		results+="{\"total\":"+size+",\"results\":[{"
				+ "\"object\":"+response.getSourceAsString()+","
						+ "\"detailed\":"+specific_source+"";
						//+ "\"detailed\":"+responseSpecific.getSourceAsString()+"";
		
		
		
		results+="}]}";
		
    	return results;
        
    }
    

	/*
	@ApiOperation(value = "Get full resource")
    @RequestMapping(method = RequestMethod.GET, path="/resource/{id}")
	@ApiImplicitParams({
		@ApiImplicitParam(
        			name = "id", 
        			value = "entity's id", 
        			required = true, 
        			dataType = "string", 
        			paramType = "path", 
        			defaultValue="10883_1009")
      })
    String runResource(@PathVariable String id) {
        
    	Settings settings = ImmutableSettings.settingsBuilder()
		        .put("cluster.name", "agroknow").build();
    	
    	Client client = new TransportClient(settings)
		        .addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
		        //.addTransportAddress(new InetSocketTransportAddress("host2", 9300));
		System.out.println("Status:"+client.settings().toString());
		// on shutdown
		
		GetResponse response = client.prepareGet("cimmyt", "resource", id)
		        .execute()
		        .actionGet();
    	
		client.close();
		
		String results="";
		
		int size=0;
		try
		{
			if(!response.getSourceAsString().isEmpty())
				size=1;
			
			results+="{\"total\":"+size+",\"results\":[{"+response.getSourceAsString()+"}]}";
		}
		catch(java.lang.NullPointerException e)
		{
			e.printStackTrace();
			results+="{\"total\":0,results:[{}]}";
		}
    	return results;
        
    }

	@ApiOperation(value = "Get full dataset/software")
    @RequestMapping(method = RequestMethod.GET, path="/dataset_software/{id}")
	@ApiImplicitParams({
		@ApiImplicitParam(
        			name = "id", 
        			value = "entity's id", 
        			required = true, 
        			dataType = "string", 
        			paramType = "path", 
        			defaultValue="11529_10066")
      })
    String runDS(@PathVariable String id) {
        
    	Settings settings = ImmutableSettings.settingsBuilder()
		        .put("cluster.name", "agroknow").build();
    	
    	Client client = new TransportClient(settings)
		        .addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
		        //.addTransportAddress(new InetSocketTransportAddress("host2", 9300));
		System.out.println("Status:"+client.settings().toString());
		// on shutdown
		
		GetResponse response = client.prepareGet("cimmyt", "dataset_software", id)
		        .execute()
		        .actionGet();
    	
		client.close();
		
		String results="";
		
		int size=0;
		try
		{
			if(!response.getSourceAsString().isEmpty())
				size=1;
			
			results+="{\"total\":"+size+",\"results\":[{"+response.getSourceAsString()+"}]}";
		}
		catch(java.lang.NullPointerException e)
		{
			e.printStackTrace();
			results+="{\"total\":0,results:[{}]}";
		}
    	return results;
        
    }
    

	@ApiOperation(value = "Get person's details")
    @RequestMapping(method = RequestMethod.GET, path="/person/{id}")
	@ApiImplicitParams({
		@ApiImplicitParam(
        			name = "id", 
        			value = "entity's id", 
        			required = true, 
        			dataType = "string", 
        			paramType = "path", 
        			defaultValue="2142792955")
      })
    String runPerson(@PathVariable String id) {
        
    	Settings settings = ImmutableSettings.settingsBuilder()
		        .put("cluster.name", "agroknow").build();
    	
    	Client client = new TransportClient(settings)
		        .addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
		        //.addTransportAddress(new InetSocketTransportAddress("host2", 9300));
		System.out.println("Status:"+client.settings().toString());
		// on shutdown
		
		GetResponse response = client.prepareGet("cimmyt", "person", id)
		        .execute()
		        .actionGet();
    	
		client.close();
		
		String results="";
		
		int size=0;
		try
		{
			if(!response.getSourceAsString().isEmpty())
				size=1;
			
			results+="{\"total\":"+size+",\"results\":[{"+response.getSourceAsString()+"}]}";
		}
		catch(java.lang.NullPointerException e)
		{
			e.printStackTrace();
			results+="{\"total\":0,results:[{}]}";
		}
    	return results;
        
    }


	@ApiOperation(value = "Get organization's details")
    @RequestMapping(method = RequestMethod.GET, path="/organization/{id}")
	@ApiImplicitParams({
		@ApiImplicitParam(
        			name = "id", 
        			value = "entity's id", 
        			required = true, 
        			dataType = "string", 
        			paramType = "path", 
        			defaultValue="1987940897")
      })
    String runOrganization(@PathVariable String id) {
        
    	Settings settings = ImmutableSettings.settingsBuilder()
		        .put("cluster.name", "agroknow").build();
    	
    	Client client = new TransportClient(settings)
		        .addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
		        //.addTransportAddress(new InetSocketTransportAddress("host2", 9300));
		System.out.println("Status:"+client.settings().toString());
		// on shutdown
		
		GetResponse response = client.prepareGet("cimmyt", "organization", id)
		        .execute()
		        .actionGet();
    	
		client.close();
		
		String results="";
		
		int size=0;
		try
		{
			if(!response.getSourceAsString().isEmpty())
				size=1;
			
			results+="{\"total\":"+size+",\"results\":[{"+response.getSourceAsString()+"}]}";
		}
		catch(java.lang.NullPointerException e)
		{
			e.printStackTrace();
			results+="{\"total\":0,results:[{}]}";
		}
    	return results;
        
    }


	@ApiOperation(value = "Get collection's details")
    @RequestMapping(method = RequestMethod.GET, path="/collection/{id}")
	@ApiImplicitParams({
		@ApiImplicitParam(
        			name = "id", 
        			value = "entity's id", 
        			required = true, 
        			dataType = "string", 
        			paramType = "path", 
        			defaultValue="403613914")
      })
    String runCollection(@PathVariable String id) {
        
    	Settings settings = ImmutableSettings.settingsBuilder()
		        .put("cluster.name", "agroknow").build();
    	
    	Client client = new TransportClient(settings)
		        .addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
		        //.addTransportAddress(new InetSocketTransportAddress("host2", 9300));
		System.out.println("Status:"+client.settings().toString());
		// on shutdown
		
		GetResponse response = client.prepareGet("cimmyt", "collection", id)
		        .execute()
		        .actionGet();
    	
		client.close();
		
		String results="";
		
		int size=0;
		try
		{
			if(!response.getSourceAsString().isEmpty())
				size=1;
			
			results+="{\"total\":"+size+",\"results\":[{"+response.getSourceAsString()+"}]}";
		}
		catch(java.lang.NullPointerException e)
		{
			e.printStackTrace();
			results+="{\"total\":0,results:[{}]}";
		}
    	return results;
        
    }
	*/


}
