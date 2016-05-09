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
    			defaultValue="CIMMYT"),
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
    			defaultValue="Books and Monographs"),
		@ApiImplicitParam(
    			name = "author", 
    			value = "", 
    			required = false, 
    			dataType = "string", 
    			paramType = "query", 
    			defaultValue="Crossa, J."),
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
			
			BoolQueryBuilder build =QueryBuilders.boolQuery();
			
			QueryBuilder query = null;
			
			String keyword=parser.parseKeyword(request);
			if(!keyword.isEmpty())
			{
				//build.must(QueryBuilders.multiMatchQuery(keyword,"title.value^2","description.value"));
				
				build.must(QueryBuilders
						.queryString(keyword)
						.defaultField("object.title.value")
						//.field("object.description.value")
						);
				
					/*.should(QueryBuilders.matchQuery("title.value", keyword))
					.should(QueryBuilders.matchQuery("description.value",keyword));*/
			}
			
			String entity_type=parser.parseEntityType(request);
			if(!entity_type.isEmpty())
				build.must(QueryBuilders.termQuery("object.type", entity_type));
			
			String type=parser.parseType(request);
			if(!type.isEmpty())
				build.must(QueryBuilders.matchQuery("type", type));
			
			String author=parser.parseAuthor(request);
			if(!author.isEmpty())
				build.must(QueryBuilders.matchQuery("creator.value", author));
			
			BuildSearchResponse builder=new BuildSearchResponse();
			results=builder.buildFrom(client,build,page);
		
			//results=builder.toString();
			
		client.close();
		
		//results="";
		return results;
	    
	}
}
