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
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.facet.FacetBuilders;
import org.elasticsearch.search.facet.terms.TermsFacet;
import org.elasticsearch.search.facet.terms.TermsFacetBuilder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.agroknow.cimmyt.utils.BuildSearchResponse;
import com.agroknow.cimmyt.utils.ParseGET;
import com.agroknow.cimmyt.utils.ToXML;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
public class FacetEndpoint {

	
	@RequestMapping(value="/facet/entity-types", method={RequestMethod.GET})
	@ApiOperation(value = "Facet for all entity types")
	@ApiImplicitParams({
        @ApiImplicitParam(
    			name = "format", 
    			value = "output format", 
    			required = true, 
    			dataType = "string", 
    			paramType = "query",
    			defaultValue="json")
      })
    String getAllET(HttpServletRequest request) {
        
    	Settings settings = ImmutableSettings.settingsBuilder()
		        .put("cluster.name", "agroknow").build();
    	
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
		
		String format;
		ParseGET parser=new ParseGET();
		format=parser.parseFormat(request);
		if(format.equals("xml"))
		{
			ToXML converter=new ToXML();
			results=converter.convertToXMLFacet(results);
		}
		

    	return results;
        
    }
	@RequestMapping(value="/facet/collections", method={RequestMethod.GET})
	@ApiOperation(value = "Facet for all collections")
	@ApiImplicitParams({
        @ApiImplicitParam(
    			name = "format", 
    			value = "output format", 
    			required = true, 
    			dataType = "string", 
    			paramType = "query",
    			defaultValue="json")
      })
    String getAllColls(HttpServletRequest request) {
        
    	Settings settings = ImmutableSettings.settingsBuilder()
		        .put("cluster.name", "agroknow").build();
    	
    	Client client = new TransportClient(settings)
		        .addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
		        //.addTransportAddress(new InetSocketTransportAddress("host2", 9300));
		System.out.println("Status:"+client.settings().toString());
		// on shutdown
		String results="";
				
			TermsFacetBuilder facet =
					FacetBuilders.termsFacet("collections").field("collection.id").size(9999);
			
			//BoolQueryBuilder bool_q=QueryBuilders.boolQuery();
			//bool_q.must(QueryBuilders.termQuery("object.type", "collection"));
			
			SearchResponse response=
					client.prepareSearch("cimmyt")
					//.setTypes("dataset_software","resource")
					//.setQuery(bool_q)
					//.setSearchType(SearchType.SCAN)
					//.setScroll(new TimeValue(60000))
					.setQuery(QueryBuilders.matchAllQuery())
					.addAggregation(AggregationBuilders.terms("collections").field("collection.id")
	                		.size(9999).order(Terms.Order.count(false)))
					.setFrom(0)
					.setSize(10)
					.execute().actionGet();
			
			TermsFacet f = null;//=(TermsFacet) response.getFacets().facetsAsMap().get("collections");
			String facet_name="collections";
			
			BuildSearchResponse builder=new BuildSearchResponse();
			results=builder.buildFrom(client,f, response, facet_name);
		
		client.close();
		
		String format;
		ParseGET parser=new ParseGET();
		format=parser.parseFormat(request);
		if(format.equals("xml"))
		{
			ToXML converter=new ToXML();
			results=converter.convertToXMLFacet(results);
		}
		

    	return results;
        
    }

	@RequestMapping(value="/facet/subjects", method={RequestMethod.GET})
	@ApiOperation(value = "Facet for all subjects")
	@ApiImplicitParams({
        @ApiImplicitParam(
    			name = "format", 
    			value = "output format", 
    			required = true, 
    			dataType = "string", 
    			paramType = "query",
    			defaultValue="json")
      })
	String getAllSubjects(HttpServletRequest request) {
        
    	Settings settings = ImmutableSettings.settingsBuilder()
		        .put("cluster.name", "agroknow").build();
    	
    	Client client = new TransportClient(settings)
		        .addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
		        //.addTransportAddress(new InetSocketTransportAddress("host2", 9300));
		System.out.println("Status:"+client.settings().toString());
		// on shutdown
		String results="";
				
			TermsFacetBuilder facet =
					FacetBuilders.termsFacet("subjects").field("subject.value").size(9999);
			
			SearchResponse response=
					client.prepareSearch("cimmyt")
					.setTypes("object")
					.setSearchType(SearchType.SCAN)
					.setScroll(new TimeValue(60000))
					.setQuery(QueryBuilders.matchAllQuery())
					.addFacet(facet)
					.execute().actionGet();
			
			TermsFacet f=(TermsFacet) response.getFacets()
					.facetsAsMap().get("subjects");
			String facet_name="subjects";
			
			BuildSearchResponse builder=new BuildSearchResponse();
			results=builder.buildFrom(client,f, response, facet_name);
		
		client.close();
		
		String format;
		ParseGET parser=new ParseGET();
		format=parser.parseFormat(request);
		if(format.equals("xml"))
		{
			ToXML converter=new ToXML();
			results=converter.convertToXMLFacet(results);
		}
				
    	return results;
        
    }
	


	@RequestMapping(value="/facet/languages", method={RequestMethod.GET})
	@ApiOperation(value = "Facet for languages")
	@ApiImplicitParams({
        @ApiImplicitParam(
    			name = "format", 
    			value = "output format", 
    			required = true, 
    			dataType = "string", 
    			paramType = "query",
    			defaultValue="json")
      })
	String getAllLangs(HttpServletRequest request) {
        
    	Settings settings = ImmutableSettings.settingsBuilder()
		        .put("cluster.name", "agroknow").build();
    	
    	Client client = new TransportClient(settings)
		        .addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
		        //.addTransportAddress(new InetSocketTransportAddress("host2", 9300));
		System.out.println("Status:"+client.settings().toString());
		// on shutdown
		String results="";
				
			TermsFacetBuilder facet =
					FacetBuilders.termsFacet("langs").field("language.value").size(9999);
			
			SearchResponse response=
					client.prepareSearch("cimmyt")
					.setTypes("object")
					.setSearchType(SearchType.SCAN)
					.setScroll(new TimeValue(60000))
					.setQuery(QueryBuilders.matchAllQuery())
					.addFacet(facet)
					.execute().actionGet();
			
			TermsFacet f=(TermsFacet) response.getFacets()
					.facetsAsMap().get("langs");
			String facet_name="langs";
			
			BuildSearchResponse builder=new BuildSearchResponse();
			results=builder.buildFrom(client,f, response, facet_name);
		
		client.close();

		String format;
		ParseGET parser=new ParseGET();
		format=parser.parseFormat(request);
		if(format.equals("xml"))
		{
			ToXML converter=new ToXML();
			results=converter.convertToXMLFacet(results);
		}
		

		return results;
        
    }
	

	@RequestMapping(value="/facet/locations", method={RequestMethod.GET})
	@ApiOperation(value = "Facet for all locations")
	@ApiImplicitParams({
        @ApiImplicitParam(
    			name = "format", 
    			value = "output format", 
    			required = true, 
    			dataType = "string", 
    			paramType = "query",
    			defaultValue="json")
      })
	String getAllLocations(HttpServletRequest request) {
        
    	Settings settings = ImmutableSettings.settingsBuilder()
		        .put("cluster.name", "agroknow").build();
    	
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
		

		String format;
		ParseGET parser=new ParseGET();
		format=parser.parseFormat(request);
		if(format.equals("xml"))
		{
			ToXML converter=new ToXML();
			results=converter.convertToXMLFacet(results);
		}
		
		//results="";
    	return results;
        
    }


	@RequestMapping(value="/facet/authors", method={RequestMethod.GET})
	@ApiOperation(value = "Facet for all authors")
	@ApiImplicitParams({
        @ApiImplicitParam(
    			name = "format", 
    			value = "output format", 
    			required = true, 
    			dataType = "string", 
    			paramType = "query",
    			defaultValue="json")
      })
	String getAllAuthors(HttpServletRequest request) {
        
    	Settings settings = ImmutableSettings.settingsBuilder()
		        .put("cluster.name", "agroknow").build();
    	
    	Client client = new TransportClient(settings)
		        .addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
		        //.addTransportAddress(new InetSocketTransportAddress("host2", 9300));
		System.out.println("Status:"+client.settings().toString());
		// on shutdown
		String results="";
				
			TermsFacetBuilder facet =
					FacetBuilders.termsFacet("authors").field("creator.value").size(99999);
			
			SearchResponse response=
					client.prepareSearch("cimmyt")
					.setTypes("resource", "dataset_software")
					.setSearchType(SearchType.SCAN)
					.setScroll(new TimeValue(60000))
					.setQuery(QueryBuilders.matchAllQuery())
					.addFacet(facet)
					.execute().actionGet();
			
			TermsFacet f=(TermsFacet) response.getFacets()
					.facetsAsMap().get("authors");
			String facet_name="authors";
			
			BuildSearchResponse builder=new BuildSearchResponse();
			results=builder.buildFrom(client,f, response, facet_name);
		
		client.close();

		String format;
		ParseGET parser=new ParseGET();
		format=parser.parseFormat(request);
		if(format.equals("xml"))
		{
			ToXML converter=new ToXML();
			results=converter.convertToXMLFacet(results);
		}
		
		//results="";
    	return results;
        
    }

	@RequestMapping(value="/facet/relations", method={RequestMethod.GET})
	@ApiOperation(value = "Facet for all relations")
	@ApiImplicitParams({
        @ApiImplicitParam(
    			name = "format", 
    			value = "output format", 
    			required = true, 
    			dataType = "string", 
    			paramType = "query",
    			defaultValue="json")
      })
	String getAllRelations(HttpServletRequest request) {
        
    	Settings settings = ImmutableSettings.settingsBuilder()
		        .put("cluster.name", "agroknow").build();
    	
    	Client client = new TransportClient(settings)
		        .addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
		        //.addTransportAddress(new InetSocketTransportAddress("host2", 9300));
		System.out.println("Status:"+client.settings().toString());
		// on shutdown
		String results="";
			
			TermsFacetBuilder facet =
					FacetBuilders.termsFacet("relations").field("relation").size(99999);
			
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
					.facetsAsMap().get("relations");
			String facet_name="relations";
			
			BuildSearchResponse builder=new BuildSearchResponse();
			results=builder.buildFrom(client,f, response, facet_name);
		
		client.close();

		String format;
		ParseGET parser=new ParseGET();
		format=parser.parseFormat(request);
		if(format.equals("xml"))
		{
			ToXML converter=new ToXML();
			results=converter.convertToXMLFacet(results);
		}
		
		//results="";
    	return results;
        
    }

	
	
}
