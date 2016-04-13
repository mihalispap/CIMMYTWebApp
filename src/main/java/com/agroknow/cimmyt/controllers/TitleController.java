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
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.agroknow.cimmyt.utils.BuildSearchResponse;

import org.springframework.web.bind.annotation.RequestMethod;


@RestController
class TitleController {
    
    @RequestMapping("/cimmyt/title/{title}")
    String run(@PathVariable String title) {
        
    	Settings settings = ImmutableSettings.settingsBuilder()
		        .put("cluster.name", "cimmyt").build();
    	
    	Client client = new TransportClient(settings)
		        .addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
		        //.addTransportAddress(new InetSocketTransportAddress("host2", 9300));
		System.out.println("Status:"+client.settings().toString());
		// on shutdown
		
		SearchResponse response=client.prepareSearch("cimmyt")
				.setTypes("object")
				.setSearchType(SearchType.SCAN)
				.setScroll(new TimeValue(60000))
				.setQuery(QueryBuilders.matchQuery("title.value", title))
				.execute().actionGet();

		BuildSearchResponse builder=new BuildSearchResponse();
		String results=builder.buildFrom(client,response);
		
		client.close();
		
		
    	return results;
        
    }
}