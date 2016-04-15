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
import org.elasticsearch.index.query.AndFilterBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FilterBuilder;
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

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import org.springframework.web.bind.annotation.RequestMethod;


@RestController
public class TitleController {

	@RequestMapping( value="/entity", method={RequestMethod.GET})
	@ApiOperation(value = "Search entities with a given title"
			+ ", subject, type")
	@ApiImplicitParams({
        @ApiImplicitParam(
        			name = "subject", 
        			value = "a subject's name", 
        			required = false, 
        			dataType = "string", 
        			paramType = "query", 
        			defaultValue="wheat"),
        @ApiImplicitParam(
    			name = "type", 
    			value = "an object's type name", 
    			required = false, 
    			dataType = "string", 
    			paramType = "query", 
    			defaultValue="resource"),
        @ApiImplicitParam(
    			name = "title", 
    			value = "part of an object's title", 
    			required = false, 
    			dataType = "string", 
    			paramType = "query", 
    			defaultValue="assessment")
      })
    String run(HttpServletRequest request) {
        
    	Settings settings = ImmutableSettings.settingsBuilder()
		        .put("cluster.name", "agroknow").build();
    	
    	Client client = new TransportClient(settings)
		        .addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
		        //.addTransportAddress(new InetSocketTransportAddress("host2", 9300));
		System.out.println("Status:"+client.settings().toString());
		// on shutdown
		String results="";
		
		String title="";
		String type="";
		String subject="";
		
		ParseGET parser=new ParseGET();
		
		type=parser.parseType(request);
		title=parser.parseTitle(request);
		subject=parser.parseSubject(request);
		
		if(title.isEmpty() && type.isEmpty() && subject.isEmpty())
			return "{\"total\":0,\"results\":[]}";
		
		if(!title.isEmpty() && type.isEmpty() && subject.isEmpty())
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
		else if(!title.isEmpty() && !type.isEmpty() && subject.isEmpty())
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
		else if(!title.isEmpty() && type.isEmpty() && !subject.isEmpty())
		{
			FilteredQueryBuilder build=
					QueryBuilders.filteredQuery(QueryBuilders.matchQuery("title.value", title), 
							FilterBuilders.termFilter("subject.value", subject));
			
			
			SearchResponse response=client.prepareSearch("cimmyt")
					.setTypes("object")
					.setSearchType(SearchType.SCAN)
					.setScroll(new TimeValue(60000))
					.setQuery(build)
					.execute().actionGet();
			
						
			BuildSearchResponse builder=new BuildSearchResponse();
			results=builder.buildFrom(client,response);
		}
		else if(title.isEmpty() && !type.isEmpty() && !subject.isEmpty())
		{
			FilteredQueryBuilder build=
					QueryBuilders.filteredQuery(QueryBuilders.matchQuery("subject.value", subject), 
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
		else if(title.isEmpty() && !type.isEmpty() && subject.isEmpty())
		{
			SearchResponse response=client.prepareSearch("cimmyt")
					.setTypes("object")
					.setSearchType(SearchType.SCAN)
					.setScroll(new TimeValue(60000))
					.setQuery(QueryBuilders.matchQuery("type", type))
					.execute().actionGet();
	
			BuildSearchResponse builder=new BuildSearchResponse();
			results=builder.buildFrom(client,response);
		}
		else if(title.isEmpty() && type.isEmpty() && !subject.isEmpty())
		{
			SearchResponse response=client.prepareSearch("cimmyt")
					.setTypes("object")
					.setSearchType(SearchType.SCAN)
					.setScroll(new TimeValue(60000))
					.setQuery(QueryBuilders.matchQuery("subject.value", subject))
					.execute().actionGet();
	
			BuildSearchResponse builder=new BuildSearchResponse();
			results=builder.buildFrom(client,response);
			
		}
		else if(!title.isEmpty() && !type.isEmpty() && !subject.isEmpty())
		{
			
			/*BoolQueryBuilder build =QueryBuilders.boolQuery()
					.must(QueryBuilders.matchQuery("title", title))
					.must(QueryBuilders.matchQuery("subject.value",subject))
					.must(QueryBuilders.matchQuery("type", type));*/

			AndFilterBuilder and_filter=FilterBuilders.andFilter();
			
			and_filter
				.add(FilterBuilders.termFilter("title", title))
				.add(FilterBuilders.termFilter("type", type))
				.add(FilterBuilders.termFilter("subject.value", subject))
				;
			
			/*FilteredQueryBuilder build=
					QueryBuilders.filteredQuery(QueryBuilders.matchQuery("subject.value", subject), 
							and_filter);*/
			
			BoolQueryBuilder build =QueryBuilders.boolQuery()
					.must(QueryBuilders.matchQuery("title.value", title))
					.must(QueryBuilders.matchQuery("subject.value",subject))
					.must(QueryBuilders.matchQuery("type", type));
			
			
			SearchResponse response=client.prepareSearch("cimmyt")
					.setTypes("object")
					.setSearchType(SearchType.SCAN)
					.setScroll(new TimeValue(60000))
					.setQuery(build)
					.setExplain(true)
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