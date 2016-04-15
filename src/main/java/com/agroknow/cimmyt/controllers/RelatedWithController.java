package com.agroknow.cimmyt.controllers;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.agroknow.cimmyt.utils.BuildSearchResponse;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
public class RelatedWithController {

	@RequestMapping(value="/related-with/{relation}", method={RequestMethod.GET})
	@ApiOperation(value = "Get resources/datasets/softwares that have the given relation")
	@ApiImplicitParams({
        @ApiImplicitParam(
        			name = "relation", 
        			value = "a relation's name", 
        			required = true, 
        			dataType = "string", 
        			paramType = "path", 
        			defaultValue="CIMMYT Program : Genetic Resources Program")
      })
    String runType(@PathVariable String relation) {
        
    	Settings settings = ImmutableSettings.settingsBuilder()
		        .put("cluster.name", "agroknow").build();
    	
    	Client client = new TransportClient(settings)
		        .addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
		        //.addTransportAddress(new InetSocketTransportAddress("host2", 9300));
		System.out.println("Status:"+client.settings().toString());
		// on shutdown
		String results="";
				
			SearchResponse response=client.prepareSearch("cimmyt")
					.setTypes("resource","dataset_software")
					.setSearchType(SearchType.SCAN)
					.setScroll(new TimeValue(60000))
					.setQuery(QueryBuilders.matchQuery("relation", relation))
					.execute().actionGet();
	
			BuildSearchResponse builder=new BuildSearchResponse();
			results=builder.buildFrom(client,response);
		
		client.close();
		
		//results="";
    	return results;
        
    }
	
}
