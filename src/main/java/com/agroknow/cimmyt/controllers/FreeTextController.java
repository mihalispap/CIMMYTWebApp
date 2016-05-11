package com.agroknow.cimmyt.controllers;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.HasChildQueryBuilder;
import org.elasticsearch.index.query.HasParentQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermFilterBuilder;
import org.elasticsearch.index.query.TermsFilterBuilder;
import org.elasticsearch.search.SearchHit;
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
    			required = false, 
    			dataType = "string", 
    			paramType = "query", 
    			defaultValue="Additive"),
		@ApiImplicitParam(
    			name = "entity-type", 
    			value = "", 
    			required = false, 
    			dataType = "string", 
    			paramType = "query", 
    			defaultValue="resource"),
		@ApiImplicitParam(
    			name = "type", 
    			value = "", 
    			required = false, 
    			dataType = "string", 
    			paramType = "query", 
    			defaultValue="Journal article"),
		@ApiImplicitParam(
    			name = "from", 
    			value = "", 
    			required = false, 
    			dataType = "string", 
    			paramType = "query", 
    			defaultValue="2012"),
		@ApiImplicitParam(
    			name = "to", 
    			value = "", 
    			required = false, 
    			dataType = "string", 
    			paramType = "query", 
    			defaultValue="2016"),
		@ApiImplicitParam(
    			name = "subject", 
    			value = "", 
    			required = false, 
    			dataType = "string", 
    			paramType = "query", 
    			defaultValue="genotypesANDplant breedingANDzea mays"),
		@ApiImplicitParam(
    			name = "collection",
    			value = "", 
    			required = false, 
    			dataType = "string", 
    			paramType = "query", 
    			defaultValue="Genetic Resources"),
		@ApiImplicitParam(
    			name = "author", 
    			value = "", 
    			required = false, 
    			dataType = "string", 
    			paramType = "query", 
    			defaultValue="Crossa, J."),
		@ApiImplicitParam(
    			name = "language", 
    			value = "", 
    			required = false, 
    			dataType = "string", 
    			paramType = "query", 
    			defaultValue="eng"),
		@ApiImplicitParam(
    			name = "location", 
    			value = "", 
    			required = false, 
    			dataType = "string", 
    			paramType = "query", 
    			defaultValue="Mexico"),
		@ApiImplicitParam(
    			name = "relation", 
    			value = "", 
    			required = false, 
    			dataType = "string", 
    			paramType = "query", 
    			defaultValue=""),
		@ApiImplicitParam(
    			name = "page", 
    			value = "page of the results (0,1...)", 
    			required = false, 
    			dataType = "integer", 
    			paramType = "query", 
    			defaultValue="0")
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
		
		
		//if(keyword.isEmpty())
		//	return "{\"total\":0"+
		//			",\"results\":[]}";
		
		String results="";
				
			//QueryBuilder query=QueryBuilders.multiMatchQuery(keyword, 
			//		"title.value^2","description.value");
			
			/*BoolQueryBuilder build =QueryBuilders.boolQuery()
					.should(QueryBuilders.matchQuery("title.value", keyword))
					.should(QueryBuilders.matchQuery("description.value",keyword));
			
			SearchResponse response=client.prepareSearch("cimmyt")
					.setTypes("object")
					//.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
					.setSearchType(SearchType.SCAN)
					.setScroll(new TimeValue(60000))
					.setQuery(build)
					.execute()
					.actionGet();
			*/
			int page=parser.parsePage(request);
			
			boolean search_parent=false;
			
			BoolQueryBuilder build =QueryBuilders.boolQuery();
			
			QueryBuilder query = null;


			BoolQueryBuilder build_o =QueryBuilders.boolQuery();

		    List<FilterBuilder> filters=new LinkedList<>();
		    
		    //filters.add(FilterBuilders.termFilter("location.value","Ethiopia"));
		    
			String keyword=parser.parseKeyword(request);
			if(!keyword.isEmpty())
			{
				search_parent=true;
				build_o.must(QueryBuilders
						.queryString(keyword)
						.field("object.title.value")
						.field("object.description.value")
						//.defaultField("object.title.value")
						//.field("object.description.value")
						);
			}
			
			String entity_type=parser.parseEntityType(request);
			if(!entity_type.isEmpty())
			{
				search_parent=true;
				String values[]=entity_type.split("AND");
				
				for(int i=0;i<values.length;i++)
					build_o.must(QueryBuilders.termQuery("object.type", values[i]));
			}
			
			String type=parser.parseType(request);
			if(!type.isEmpty())
			{
				String values[]=type.split("AND");
				
				for(int i=0;i<values.length;i++)
					filters.add(FilterBuilders.termFilter("type",values[i]));
			}
				//filters.add(FilterBuilders.termFilter("type",type));
				//build.must(QueryBuilders.matchQuery("type", type));

			String author=parser.parseAuthor(request);
			if(!author.isEmpty())
			{
				String values[]=author.split("AND");
				
				for(int i=0;i<values.length;i++)
					filters.add(FilterBuilders.termFilter("creator.value",values[i]));
			}	
			
			String collection=parser.parseCollection(request);
			if(!collection.isEmpty())
			{
				String values[]=collection.split("AND");
				
				for(int i=0;i<values.length;i++)
				{
					BoolQueryBuilder bool_build =QueryBuilders.boolQuery()
							.must(QueryBuilders.matchQuery("object.title.value", values[i]))
							.must(QueryBuilders.matchQuery("object.type","collection"));
							
						SearchResponse response=client.prepareSearch("cimmyt")
								.setTypes("object")
								.setSearchType(SearchType.SCAN)
								.setScroll(new TimeValue(60000))
								.setQuery(bool_build)
								.setSize(1)
								.execute()
								.actionGet();
					//System.out.println(bool_build.toString());
						
					try
					{
						for(SearchHit hit : response.getHits().getHits())
						{
							filters.add(FilterBuilders.termFilter("collection.id",
								hit.getId()));
							break;
						}
					}
					catch(java.lang.ArrayIndexOutOfBoundsException e)
					{
						filters.add(FilterBuilders.termFilter("collection.id",
								"-999"));
					}
					
					if(response.getHits().getTotalHits()==0)
						filters.add(FilterBuilders.termFilter("collection.id",
								"-999"));
				}
			}			
			
			String subject=parser.parseSubject(request);
			if(!subject.isEmpty())
			{
				search_parent=true;
				String values[]=subject.split("AND");
				
				for(int i=0;i<values.length;i++)
					build_o.must(QueryBuilders.termQuery("subject.value", values[i]));
			}
				//build_o.must(QueryBuilders.matchQuery("subject.value", subject));
			
			String lang=parser.parseLanguage(request);
			if(!lang.isEmpty())
			{
				search_parent=true;
				String values[]=lang.split("AND");
				
				for(int i=0;i<values.length;i++)
					build_o.must(QueryBuilders.termQuery("language.value", values[i]));
			}
				//build_o.must(QueryBuilders.matchQuery("language.value", lang));
			
			String location=parser.parseLocation(request);
			if(!location.isEmpty())
			{
				String values[]=location.split("AND");
				
				for(int i=0;i<values.length;i++)
					filters.add(FilterBuilders.termFilter("location.value",values[i]));
			}
				//filters.add(FilterBuilders.termFilter("location.value",location));
				//build.must(QueryBuilders.matchQuery("location.value", location));
			
			String relation=parser.parseRelation(request);
			if(!relation.isEmpty())
			{
				String values[]=relation.split("AND");
				
				for(int i=0;i<values.length;i++)
					filters.add(FilterBuilders.termFilter("relation",values[i]));
			}
				//filters.add(FilterBuilders.termFilter("relation",relation));
				//build.must(QueryBuilders.matchQuery("relation", relation));

			String from_date=parser.parseFromDate(request);
			String to_date=parser.parseToDate(request);
			if(!from_date.isEmpty() || !to_date.isEmpty())
			{
								
				/*if(from_date.isEmpty())
					from_date=to_date;
				if(to_date.isEmpty())
					to_date=from_date;*/
				
				if(from_date.isEmpty())
					from_date="50";
				if(to_date.isEmpty())
					to_date="9999";
				
				//if(from_date.equals(to_date))
				//{
					from_date+="-01-01";
					to_date+="-12-31";
				//}
				
				filters.add(FilterBuilders
						.rangeFilter("date")
						.gte(from_date)
						.lte(to_date));
			}
			
			
			/*if(1==0)
			{
			BuildSearchResponse builder=new BuildSearchResponse();
			results=builder.buildFrom(client,build,page);
			}
			*/
			if(1==0)
			{
				//create the searchRequestBuilder object.
			    SearchRequestBuilder searchRequestBuilder = new SearchRequestBuilder(client)
			    		.setIndices("cimmyt");

			    //Query 1. Search on all books that have the term 'book' in the title and 
			    //return the 'authors'.
			    HasChildQueryBuilder bookNameQuery = 
			    		QueryBuilders.hasChildQuery("dataset_software", 
			    				QueryBuilders.matchQuery("location.value", "Mexico"));
			    System.out.println("Exectuing Query 1");
			    SearchResponse searchResponse1 = 
			    		searchRequestBuilder.setQuery(bookNameQuery).execute().actionGet();
			    System.out.println("There were " + searchResponse1.getHits().getTotalHits()  + 
			    		" results found for Query 1.");
			    //System.out.println(searchResponse1.toString());
			    System.out.println();

			    //Query 2. Search on all authors that have the terms 'jon doe' in 
			    //the name and return the 'books'.
			    HasParentQueryBuilder authorNameQuery = 
			    		QueryBuilders.hasParentQuery("object", 
			    				QueryBuilders.matchQuery("title.value", "Evaluation"));
			    System.out.println("Exectuing Query 2");
			    SearchResponse searchResponse2 = 
			    		searchRequestBuilder.setQuery(authorNameQuery).execute().actionGet();
			    System.out.println("There were " + 
			    		searchResponse2.getHits().getTotalHits()  + " results found for Query 2.");
			    //System.out.println(searchResponse2.toString());
			    System.out.println();

			    //Query 3. Search for books written by 'jane smith' and type Fiction.
			    //Could get all ids of object and apply aggregation there OR go @below solution
			    /*TermFilterBuilder termFilter = FilterBuilders.termFilter("location.value", "Mexico");
			    HasParentQueryBuilder authorNameQuery2 = 
			    		QueryBuilders.hasParentQuery("object", 
			    				QueryBuilders.matchQuery("title.value", "Evaluation"));
			    
			    SearchResponse searchResponse3 = 
			    		searchRequestBuilder.setQuery(QueryBuilders.filteredQuery(
			    				authorNameQuery2, termFilter))
			    		.addAggregation(AggregationBuilders.terms("appids").field("appid")
		                		.size(9999).order(Terms.Order.count(false)))
			    		//.addAggregation(AggregationBuilders.terms("subjects").field("_parent.subject.value")
		                //		.size(9999).order(Terms.Order.count(false)))
		                .addAggregation(AggregationBuilders.terms("authors").field("creator.value")
		                		.size(9999).order(Terms.Order.count(false)))
			    		.execute()
			    		.actionGet();
			    //PERFECT
			    SearchResponse searchResponse5=
			    		searchRequestBuilder
			    		.setQuery(QueryBuilders.idsQuery().ids("11529_10066","11529_10565"))
			    		//.addAggregation(AggregationBuilders.terms("appids").field("appid")
		                //		.size(9999).order(Terms.Order.count(false)))
			    		.addAggregation(AggregationBuilders.terms("subjects2").field("subject.value")
		                		.size(9999).order(Terms.Order.count(false)))
		                //.addAggregation(AggregationBuilders.terms("authors").field("creator.value")
		                //		.size(9999).order(Terms.Order.count(false)))
			    		.execute()
			    		.actionGet();
			    
			    HasChildQueryBuilder bookNameQuery22 = 
			    		QueryBuilders.hasChildQuery("resource", 
			    				QueryBuilders.matchQuery("location.value", "Mexico"));
			    TermFilterBuilder termFilter2 = FilterBuilders.termFilter("title.value", "evaluation");
			    System.out.println("Exectuing Query 4");
			    SearchResponse searchResponse4 = 
			    		searchRequestBuilder.setQuery(QueryBuilders.filteredQuery(
			    				bookNameQuery22, termFilter2))
			    		.addAggregation(AggregationBuilders.terms("subjects").field("subject.value")
		                		.size(9999).order(Terms.Order.count(false)))
			    		.execute()
			    		.actionGet();
			    */
				
				if(!keyword.isEmpty())
				{
					build_o.must(QueryBuilders
							.queryString(keyword)
							.defaultField("title.value")
							//.field("object.description.value")
							);
				}
				
				if(!subject.isEmpty())
					build_o.must(QueryBuilders.matchQuery("subject.value", subject));
				
			    
			    List<FilterBuilder> filtersB=new LinkedList<>();
			    
			    filtersB.add(FilterBuilders.termFilter("location.value","Ethiopia"));
			    filtersB.add(FilterBuilders.termFilter("creator.value","Asmare Yallew"));
			}
			    
			BuildSearchResponse builder=new BuildSearchResponse();
			results=builder.buildFrom(client,build_o,filters,page,search_parent);
			//}
			//results=builder.toString();
			
		client.close();
		
		//results="";
		return results;
	    
	}
}
