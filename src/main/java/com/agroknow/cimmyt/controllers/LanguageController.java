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
import org.elasticsearch.index.query.BoolQueryBuilder;
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

//@RestController
public class LanguageController {

	@RequestMapping( value="/language", method={RequestMethod.GET})
	@ApiOperation(value = "Get objects with given language")
	@ApiImplicitParams({
        @ApiImplicitParam(
        			name = "iso", 
        			value = "ISO639-3 code of the language", 
        			required = true, 
        			dataType = "string", 
        			paramType = "query", 
        			defaultValue="spa")
      })
    String run(HttpServletRequest request) { 
		Settings settings = ImmutableSettings.settingsBuilder()
		        .put("cluster.name", "agroknow").build();
		
		Client client = new TransportClient(settings)
		        .addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
		
		ParseGET parser=new ParseGET();
		String uri=parser.parseISO(request);
		
		if(uri.isEmpty())
			return "{\"total\":0"+
					",\"results\":[]}";
		
		String results="";
		
		QueryBuilder buildQ=
				QueryBuilders.matchQuery("language.value", uri);
		
		SearchResponse response=client.prepareSearch("cimmyt")
				.setTypes("object")
				.setSearchType(SearchType.SCAN)
				.setScroll(new TimeValue(60000))
				.setQuery(buildQ)
				.execute().actionGet();
	
			BuildSearchResponse builder=new BuildSearchResponse();
			results=builder.buildFrom(client,response);
		
		client.close();
		
		//results="";
		return results;
	    
	}
}
