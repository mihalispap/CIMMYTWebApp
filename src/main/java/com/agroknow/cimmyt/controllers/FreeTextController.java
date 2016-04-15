package com.agroknow.cimmyt.controllers;

import javax.servlet.http.HttpServletRequest;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.facet.FacetBuilders;
import org.elasticsearch.search.facet.terms.TermsFacet;
import org.elasticsearch.search.facet.terms.TermsFacetBuilder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.agroknow.cimmyt.utils.BuildSearchResponse;
import com.agroknow.cimmyt.utils.ParseGET;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
public class FreeTextController {

	@ApiOperation(value = "Search entities containing keyword")
	@RequestMapping( value="/search", method={RequestMethod.GET})
	@ApiImplicitParams({
		@ApiImplicitParam(
    			name = "keyword", 
    			value = "keyword to search entities against", 
    			required = true, 
    			dataType = "date", 
    			paramType = "query", 
    			defaultValue="2012")
	})
	String run(HttpServletRequest request) { 
		Settings settings = ImmutableSettings.settingsBuilder()
		        .put("cluster.name", "agroknow").build();
		
		Client client = new TransportClient(settings)
		        .addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
		        //.addTransportAddress(new InetSocketTransportAddress("host2", 9300));
		//System.out.println("Status:"+client.settings().toString());
		// on shutdown
		
		ParseGET parser=new ParseGET();
		String keyword=parser.parseKeyword(request);
		
		if(keyword.isEmpty())
			return "{\"total\":0"+
					",\"results\":[]}";
		
		String results="";
				
			QueryBuilder query=QueryBuilders.multiMatchQuery(keyword, 
					"title.value^2","description.value");
			SearchResponse response=client.prepareSearch("cimmyt")
					.setTypes("resource","dataset_software",
							"organization","person","object","collection")
					.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
					.setScroll(new TimeValue(60000))
					.setQuery(query)
					.execute()
					.actionGet();
	
			BuildSearchResponse builder=new BuildSearchResponse();
			results=builder.buildFrom(client,response);
		
		client.close();
		
		//results="";
		return results;
	    
	}
}
