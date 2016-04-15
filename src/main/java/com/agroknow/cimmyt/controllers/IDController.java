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

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import org.springframework.web.bind.annotation.RequestMethod;


@RestController
public class IDController {

	@ApiOperation(value = "Get object by Id", nickname = "find object values by id")
    @RequestMapping(method = RequestMethod.GET, path="/id/{id}"/*, produces = {"application/json","application/xml"}*/)
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
    	
		client.close();
		
		String results="";
		
		int size=0;
		if(!response.getSourceAsString().isEmpty())
			size=1;
		
		results+="{\"total\":"+size+",results:[{"+response.getSourceAsString()+"}]}";
		
    	return results;
        
    }
    
	@ApiOperation(value = "Get full record for entity")
    @RequestMapping(method = RequestMethod.GET, path="/{type}/{id}"/*, produces = {"application/json","application/xml"}*/)
	@ApiImplicitParams({
		@ApiImplicitParam(
    			name = "type", 
    			value = "entity's type", 
    			required = true, 
    			dataType = "string", 
    			paramType = "path", 
    			defaultValue="resource"),
        @ApiImplicitParam(
        			name = "id", 
        			value = "entity's id", 
        			required = true, 
        			dataType = "string", 
        			paramType = "path", 
        			defaultValue="10883_1009")
      })
    String runObject(@PathVariable String type,@PathVariable String id) {
        
    	Settings settings = ImmutableSettings.settingsBuilder()
		        .put("cluster.name", "agroknow").build();
    	
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
		try
		{
			if(!response.getSourceAsString().isEmpty())
				size=1;
			
			results+="{\"total\":"+size+",results:[{"+response.getSourceAsString()+"}]}";
		}
		catch(java.lang.NullPointerException e)
		{
			e.printStackTrace();
			results+="{\"total\":0,results:[{}]}";
		}
    	return results;
        
    }
    
}
