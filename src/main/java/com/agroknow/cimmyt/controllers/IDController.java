package com.agroknow.cimmyt.controllers;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;


@RestController
class IDController {

    @RequestMapping("/id/{id}")
    String run(@PathVariable String id) {
        
    	Settings settings = ImmutableSettings.settingsBuilder()
		        .put("cluster.name", "cimmyt").build();
    	
    	Client client = new TransportClient(settings)
		        .addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
		        //.addTransportAddress(new InetSocketTransportAddress("host2", 9300));
		System.out.println("Status:"+client.settings().toString());
		// on shutdown
		
		GetResponse response = client.prepareGet("cimmyt", "object", id)
		        .execute()
		        .actionGet();
    	
		client.close();
		
		String results="";
		
		int size=0;
		if(!response.getSourceAsString().isEmpty())
			size=1;
		
		results+="{\"total\":"+size+",results:[{"+response.getSourceAsString()+"}]}";
		
    	return results;
        
    }
    
    @RequestMapping("/{type}/{id}")
    String runObject(@PathVariable String type,@PathVariable String id) {
        
    	Settings settings = ImmutableSettings.settingsBuilder()
		        .put("cluster.name", "cimmyt").build();
    	
    	Client client = new TransportClient(settings)
		        .addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
		        //.addTransportAddress(new InetSocketTransportAddress("host2", 9300));
		System.out.println("Status:"+client.settings().toString());
		// on shutdown
		
		GetResponse response = client.prepareGet("cimmyt", type, id)
		        .execute()
		        .actionGet();
    	
		client.close();
		
		String results="";
		
		int size=0;
		if(!response.getSourceAsString().isEmpty())
			size=1;
		
		results+="{\"total\":"+size+",results:[{"+response.getSourceAsString()+"}]}";
		
    	return results;
        
    }
}
