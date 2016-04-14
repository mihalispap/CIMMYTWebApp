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
import org.elasticsearch.search.facet.FacetBuilders;
import org.elasticsearch.search.facet.terms.TermsFacet;
import org.elasticsearch.search.facet.terms.TermsFacetBuilder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agroknow.cimmyt.utils.BuildSearchResponse;

@RestController
public class GetAllController {

	@RequestMapping("/cimmyt/getall/entity-types")
    String getAllET() {
        
    	Settings settings = ImmutableSettings.settingsBuilder()
		        .put("cluster.name", "cimmyt").build();
    	
    	Client client = new TransportClient(settings)
		        .addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
		        //.addTransportAddress(new InetSocketTransportAddress("host2", 9300));
		System.out.println("Status:"+client.settings().toString());
		// on shutdown
		String results="";
				
			TermsFacetBuilder facet =
					FacetBuilders.termsFacet("entity_types").field("type").size(9999);
			
			SearchResponse response=
					client.prepareSearch("cimmyt")
					.setTypes("object")
					.setSearchType(SearchType.SCAN)
					.setScroll(new TimeValue(60000))
					.setQuery(QueryBuilders.matchAllQuery())
					.addFacet(facet)
					.execute().actionGet();
			
			TermsFacet f=(TermsFacet) response.getFacets()
					.facetsAsMap().get("entity_types");
			String facet_name="entity_types";
			
			BuildSearchResponse builder=new BuildSearchResponse();
			results=builder.buildFrom(client,f, response, facet_name);
		
		client.close();
		
		//results="";
    	return results;
        
    }
	

	@RequestMapping("/cimmyt/getall/locations")
    String getAllLocations() {
        
    	Settings settings = ImmutableSettings.settingsBuilder()
		        .put("cluster.name", "cimmyt").build();
    	
    	Client client = new TransportClient(settings)
		        .addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
		        //.addTransportAddress(new InetSocketTransportAddress("host2", 9300));
		System.out.println("Status:"+client.settings().toString());
		// on shutdown
		String results="";
				
			TermsFacetBuilder facet =
					FacetBuilders.termsFacet("locations").field("location.value").size(99999);
			
			SearchResponse response=
					client.prepareSearch("cimmyt")
					.setTypes("person", "organization", 
							"resource", "dataset_software")
					.setSearchType(SearchType.SCAN)
					.setScroll(new TimeValue(60000))
					.setQuery(QueryBuilders.matchAllQuery())
					.addFacet(facet)
					.execute().actionGet();
			
			TermsFacet f=(TermsFacet) response.getFacets()
					.facetsAsMap().get("locations");
			String facet_name="locations";
			
			BuildSearchResponse builder=new BuildSearchResponse();
			results=builder.buildFrom(client,f, response, facet_name);
		
		client.close();
		
		//results="";
    	return results;
        
    }
	
	
}
