package com.agroknow.cimmyt.controllers;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
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
public class AuthorController {



	@ApiOperation(value = "Get author's ")
    @RequestMapping(method = RequestMethod.GET, path="/author/{name}")
	@ApiImplicitParams({
		@ApiImplicitParam(
        			name = "name", 
        			value = "author's name", 
        			required = true, 
        			dataType = "string", 
        			paramType = "path", 
        			defaultValue="Burgueño, Juan")
      })
    String runCollection(@PathVariable String name) {
        
		Settings settings = ImmutableSettings.settingsBuilder()
		        .put("cluster.name", "agroknow").build();
    	
    	Client client = new TransportClient(settings)
		        .addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
		        //.addTransportAddress(new InetSocketTransportAddress("host2", 9300));
		//System.out.println("Status:"+client.settings().toString());
		// on shutdown
		String results="";
		
		if(name.endsWith("."))
			name+=".";
		
		char ch=name.charAt(name.length()-1);
		if(Character.isUpperCase(ch))
			name+=".";
			
		BoolQueryBuilder build =QueryBuilders.boolQuery()
				.must(QueryBuilders.matchQuery("creator.value", name));
				
			SearchResponse response=client.prepareSearch("cimmyt")
					.setTypes("resource","dataset_software")
					.setSearchType(SearchType.SCAN)
					.setScroll(new TimeValue(60000))
					.setQuery(build)
					.execute().actionGet();
	
			BuildSearchResponse builder=new BuildSearchResponse();
			results=builder.buildFrom(client,response);
		
		client.close();
		
		//results="";
    	return results;
        
    }


	
}
