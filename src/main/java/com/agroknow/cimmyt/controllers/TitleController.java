package com.agroknow.cimmyt.controllers;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

//import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.agroknow.cimmyt.utils.BuildSearchResponse;
import com.agroknow.cimmyt.utils.ParseGET;

import org.springframework.web.bind.annotation.RequestMethod;


@RestController
class TitleController {

	@RequestMapping( value="/cimmyt/title", method={RequestMethod.GET})
    String run(HttpServletRequest request) {
        
    	Settings settings = ImmutableSettings.settingsBuilder()
		        .put("cluster.name", "cimmyt").build();
    	
    	Client client = new TransportClient(settings)
		        .addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
		        //.addTransportAddress(new InetSocketTransportAddress("host2", 9300));
		System.out.println("Status:"+client.settings().toString());
		// on shutdown
		String results="";
		
		String title="";
		String type="";
		
		ParseGET parser=new ParseGET();
		
		type=parser.parseType(request);
		title=parser.parseTitle(request);
		
		if(title.isEmpty() && type.isEmpty())
			return "{\"total\":0,\"results\":[]}";
		
		if(!title.isEmpty() && type.isEmpty())
		{		
			SearchResponse response=client.prepareSearch("cimmyt")
					.setTypes("object")
					.setSearchType(SearchType.SCAN)
					.setScroll(new TimeValue(60000))
					.setQuery(QueryBuilders.matchQuery("title.value", title))
					.execute().actionGet();
	
			BuildSearchResponse builder=new BuildSearchResponse();
			results=builder.buildFrom(client,response);
		}
		else if(!title.isEmpty() && !type.isEmpty())
		{
			FilteredQueryBuilder build=
					QueryBuilders.filteredQuery(QueryBuilders.matchQuery("title.value", title), 
							FilterBuilders.termFilter("type", type));
			
			
			SearchResponse response=client.prepareSearch("cimmyt")
					.setTypes("object")
					.setSearchType(SearchType.SCAN)
					.setScroll(new TimeValue(60000))
					.setQuery(build)
					.execute().actionGet();
			
						
			BuildSearchResponse builder=new BuildSearchResponse();
			results=builder.buildFrom(client,response);
		}

		client.close();
		
		
    	return results;
        
    }
	
	/*
    @RequestMapping("/cimmyt/title/{title}")
    String run(@PathVariable String title) {
        
    	Settings settings = ImmutableSettings.settingsBuilder()
		        .put("cluster.name", "cimmyt").build();
    	
    	Client client = new TransportClient(settings)
		        .addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
		        //.addTransportAddress(new InetSocketTransportAddress("host2", 9300));
		System.out.println("Status:"+client.settings().toString());
		// on shutdown
		String results="";
				
			SearchResponse response=client.prepareSearch("cimmyt")
					.setTypes("object")
					.setSearchType(SearchType.SCAN)
					.setScroll(new TimeValue(60000))
					.setQuery(QueryBuilders.matchQuery("title.value", title))
					.execute().actionGet();
	
			BuildSearchResponse builder=new BuildSearchResponse();
			results=builder.buildFrom(client,response);
		
		client.close();
		
		//results="";
    	return results;
        
    }
    
    @RequestMapping("/cimmyt/title/{title}/{type}")
    String run(@PathVariable String title, @PathVariable String type) {
        
    	Settings settings = ImmutableSettings.settingsBuilder()
		        .put("cluster.name", "cimmyt").build();
    	
    	Client client = new TransportClient(settings)
		        .addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
		        //.addTransportAddress(new InetSocketTransportAddress("host2", 9300));
		System.out.println("Status:"+client.settings().toString());
		// on shutdown
		String results="";
		if(type.isEmpty())
		{		
			SearchResponse response=client.prepareSearch("cimmyt")
					.setTypes("object")
					.setSearchType(SearchType.SCAN)
					.setScroll(new TimeValue(60000))
					.setQuery(QueryBuilders.matchQuery("title.value", title))
					.execute().actionGet();
	
			BuildSearchResponse builder=new BuildSearchResponse();
			results=builder.buildFrom(client,response);
		}
		else
		{
			FilteredQueryBuilder build=
					QueryBuilders.filteredQuery(QueryBuilders.matchQuery("title.value", title), 
							FilterBuilders.termFilter("type", type));
			
			
			SearchResponse response=client.prepareSearch("cimmyt")
					.setTypes("object")
					.setSearchType(SearchType.SCAN)
					.setScroll(new TimeValue(60000))
					.setQuery(build)
					.execute().actionGet();
			
						
			BuildSearchResponse builder=new BuildSearchResponse();
			results=builder.buildFrom(client,response);
		}
		client.close();
		
		
    	return results;
        
    }
    */
}