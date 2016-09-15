package com.agroknow.cimmyt.controllers;

import java.io.IOException;
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
import org.elasticsearch.common.unit.Fuzziness;
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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.agroknow.cimmyt.utils.BuildSearchResponse;
import com.agroknow.cimmyt.utils.GetConfig;
import com.agroknow.cimmyt.utils.ParseGET;
import com.agroknow.cimmyt.utils.ToXML;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@CrossOrigin
@RestController
public class SearchEndpoint {

	@ApiOperation(value = "Search entities containing keyword")
	@RequestMapping( value="/search", method={RequestMethod.GET},produces="text/plain")
	@ApiImplicitParams({
		@ApiImplicitParam(
    			name = "freetext", 
    			value = "search entities based on: title, abstract, author, location, subject", 
    			required = false, 
    			dataType = "string", 
    			paramType = "query", 
    			defaultValue="crossa"),
		@ApiImplicitParam(
    			name = "keyword", 
    			value = "keyword to search entities against", 
    			required = false, 
    			dataType = "string", 
    			paramType = "query", 
    			defaultValue="Additive"),
		@ApiImplicitParam(
    			name = "entity-type", 
    			value = "limit results by entity type (eg. resource, dataset_software, person, organization, collection)", 
    			required = false, 
    			dataType = "string", 
    			paramType = "query", 
    			defaultValue="resource"),
		@ApiImplicitParam(
    			name = "type", 
    			value = "filter results by type (for resources and datasets/softwares)", 
    			required = false, 
    			dataType = "string", 
    			paramType = "query", 
    			defaultValue="Journal article"),
		@ApiImplicitParam(
    			name = "from", 
    			value = "filter results by those created after this date", 
    			required = false, 
    			dataType = "string", 
    			paramType = "query", 
    			defaultValue="1975"),
		@ApiImplicitParam(
    			name = "to", 
    			value = "filter results by those before this date", 
    			required = false, 
    			dataType = "string", 
    			paramType = "query", 
    			defaultValue="2016"),
		@ApiImplicitParam(
    			name = "subject", 
    			value = "limit results to those having the specified subject(s)", 
    			required = false, 
    			dataType = "string", 
    			paramType = "query", 
    			defaultValue="genotypesANDplant breedingANDzea mays"),
		@ApiImplicitParam(
    			name = "collection",
    			value = "limit results to those belonging in this collection(s)", 
    			required = false, 
    			dataType = "string", 
    			paramType = "query", 
    			defaultValue="Genetic Resources"),
		@ApiImplicitParam(
    			name = "author", 
    			value = "return results with the specified author", 
    			required = false, 
    			dataType = "string", 
    			paramType = "query", 
    			defaultValue="Crossa, J."),
		@ApiImplicitParam(
    			name = "language", 
    			value = "language of the results", 
    			required = false, 
    			dataType = "string", 
    			paramType = "query", 
    			defaultValue="eng"),
		@ApiImplicitParam(
    			name = "location", 
    			value = "limit the results by a specific location(s)", 
    			required = false, 
    			dataType = "string", 
    			paramType = "query", 
    			defaultValue="Mexico"),
		@ApiImplicitParam(
    			name = "relation", 
    			value = "filter results having this relation", 
    			required = false, 
    			dataType = "string", 
    			paramType = "query", 
    			defaultValue=""),
		@ApiImplicitParam(
    			name = "page", 
    			value = "page of the results (0,1...)", 
    			required = false, 
    			dataType = "int", 
    			paramType = "query", 
    			defaultValue="0"),
		@ApiImplicitParam(
    			name = "format", 
    			value = "output format", 
    			required = true, 
    			dataType = "string", 
    			paramType = "query",
    			defaultValue="json")
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
			BoolQueryBuilder build_child =QueryBuilders.boolQuery();
			BoolQueryBuilder build_enhanced=QueryBuilders.boolQuery();
			
			
		    List<FilterBuilder> filters=new LinkedList<>();
		    
		    GetConfig config=new GetConfig();
			int fuzzy;
			
			
			float similarity;
			try{
				similarity=Double.valueOf(config.getValue("similarity")).floatValue();
			}
			catch(Exception e)
			{
				similarity=(float) 0.75;
			}
				

			String keywordE=parser.parseKeywordEnhanced(request);
			if(!keywordE.isEmpty())
			{
				//search_parent=true;
				
				int fuzzy_not=0;

				try {
					fuzzy_not = Integer.valueOf(config.getValue("fuzzy_not"));
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					fuzzy_not=0;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					fuzzy_not=0;
				}
				
				
				try {
					fuzzy = Integer.valueOf(config.getValue("fuzzy_keyword"));
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					fuzzy=1;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					fuzzy=1;
				}
				
				BoolQueryBuilder bool_qN=QueryBuilders.boolQuery();
				/*bool_qN.must(QueryBuilders
						.fuzzyLikeThisQuery("creator.value",
								"object.title.value")
						.likeText(keywordE)
						.maxQueryTerms(2));*/
				/*
				bool_qN.should(QueryBuilders
						.fuzzyLikeThisQuery("title.value")
						.likeText(keywordE)
						.maxQueryTerms(2));

				bool_qN.should(QueryBuilders
						.fuzzyLikeThisQuery("description.value")
						.likeText(keywordE)
						.maxQueryTerms(2));
				
				bool_qN.should(QueryBuilders
						.fuzzyLikeThisQuery("creator.value")
						.likeText(keywordE)
						.maxQueryTerms(2));

				bool_qN.should(QueryBuilders
						.fuzzyLikeThisQuery("location.value")
						.likeText(keywordE)
						.maxQueryTerms(2));
				
				bool_qN.should(QueryBuilders
						.fuzzyLikeThisQuery("subject.value")
						.likeText(keywordE)
						.maxQueryTerms(2));
				
				
				build_enhanced.must(bool_qN);
				
				System.out.println(build_enhanced.toString());
				*/
				String or_values[]=keywordE.split("OR");
				BoolQueryBuilder bool_q=QueryBuilders.boolQuery();
				
				for(int j=0;j<or_values.length;j++)
				{
					BoolQueryBuilder bool_inner=QueryBuilders.boolQuery();
					String values[]=or_values[j].split("AND");
					
					for(int i=0;i<values.length;i++)
					{

						//System.out.println(values[i]);
						boolean has_not=false;
						
						BoolQueryBuilder bool_beta=QueryBuilders.boolQuery();
						
						if(values[i].contains("NOT"))
						{
							has_not=true;
							values[i]=values[i].replace("NOT", "");
						}
						
						if(!has_not)
						{
							if(fuzzy==1)
							{
								bool_beta.should(QueryBuilders
										.fuzzyLikeThisQuery("object.title.value")
										.fuzziness(Fuzziness.fromSimilarity((float) similarity))
										.likeText(values[i])
										.maxQueryTerms(2));

								bool_beta.should(QueryBuilders
										.fuzzyLikeThisQuery("object.description.value")
										.fuzziness(Fuzziness.fromSimilarity((float) similarity))
										.likeText(values[i])
										.maxQueryTerms(2));
								
								bool_beta.should(QueryBuilders
										.fuzzyLikeThisQuery("creator.value")
										.fuzziness(Fuzziness.fromSimilarity((float) similarity))
										.likeText(values[i])
										.maxQueryTerms(2));

								bool_beta.should(QueryBuilders
										.fuzzyLikeThisQuery("location.value")
										.fuzziness(Fuzziness.fromSimilarity((float) similarity))
										.likeText(values[i])
										.maxQueryTerms(2));
								
								bool_beta.should(QueryBuilders
										.fuzzyLikeThisQuery("subject.value")
										.fuzziness(Fuzziness.fromSimilarity((float) similarity))
										.likeText(values[i])
										.maxQueryTerms(2));
								
								
								bool_inner.must(bool_beta);
								
							}
							else
							{
								bool_beta.should(QueryBuilders
										.queryString(values[i])
										.field("object.title.value"));

								bool_beta.should(QueryBuilders
										.queryString(values[i])
										.field("object.description.value"));
								
								bool_beta.should(QueryBuilders
										.queryString(values[i])
										.field("creator.value"));

								bool_beta.should(QueryBuilders
										.queryString(values[i])
										.field("location.value"));
								
								bool_beta.should(QueryBuilders
										.queryString(values[i])
										.field("subject.value"));
								
								
								bool_inner.must(bool_beta);
								
							}
						}
						else
						{
							if(fuzzy_not==1)
							{
								
								bool_beta.mustNot(QueryBuilders
										.fuzzyLikeThisQuery("object.title.value")
										.fuzziness(Fuzziness.fromSimilarity((float) similarity))
										.likeText(values[i])
										.maxQueryTerms(2));

								bool_beta.mustNot(QueryBuilders
										.fuzzyLikeThisQuery("object.description.value")
										.fuzziness(Fuzziness.fromSimilarity((float) similarity))
										.likeText(values[i])
										.maxQueryTerms(2));
								
								bool_beta.mustNot(QueryBuilders
										.fuzzyLikeThisQuery("creator.value")
										.fuzziness(Fuzziness.fromSimilarity((float) similarity))
										.likeText(values[i])
										.maxQueryTerms(2));

								bool_beta.mustNot(QueryBuilders
										.fuzzyLikeThisQuery("location.value")
										.fuzziness(Fuzziness.fromSimilarity((float) similarity))
										.likeText(values[i])
										.maxQueryTerms(2));
								
								bool_beta.mustNot(QueryBuilders
										.fuzzyLikeThisQuery("subject.value")
										.fuzziness(Fuzziness.fromSimilarity((float) similarity))
										.likeText(values[i])
										.maxQueryTerms(2));
								
								
								bool_inner.must(bool_beta);
								
							}
							else
							{

								bool_beta.mustNot(QueryBuilders
										.queryString(values[i])
										.field("object.title.value"));

								bool_beta.mustNot(QueryBuilders
										.queryString(values[i])
										.field("object.description.value"));
								
								bool_beta.mustNot(QueryBuilders
										.queryString(values[i])
										.field("creator.value"));

								bool_beta.mustNot(QueryBuilders
										.queryString(values[i])
										.field("location.value"));
								
								bool_beta.mustNot(QueryBuilders
										.queryString(values[i])
										.field("subject.value"));
								
								
								bool_inner.must(bool_beta);
							}
						}
						
					}
					
					bool_q.should(bool_inner);
				}
				//build_o.must(bool_q);
				
				//System.out.println(bool_q.toString());
				
				build_enhanced.must(bool_q);
			}

			String keyword=parser.parseKeyword(request);
			if(!keyword.isEmpty())
			{
				search_parent=true;
				
				int fuzzy_not=0;

				try {
					fuzzy_not = Integer.valueOf(config.getValue("fuzzy_not"));
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					fuzzy_not=0;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					fuzzy_not=0;
				}
				
				
				try {
					fuzzy = Integer.valueOf(config.getValue("fuzzy_keyword"));
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					fuzzy=1;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					fuzzy=1;
				}
				
				String or_values[]=keyword.split("OR");
				BoolQueryBuilder bool_q=QueryBuilders.boolQuery();
				
				for(int j=0;j<or_values.length;j++)
				{
					BoolQueryBuilder bool_inner=QueryBuilders.boolQuery();
					String values[]=or_values[j].split("AND");
					
					for(int i=0;i<values.length;i++)
					{

						boolean has_not=false;
						
						if(values[i].contains("NOT"))
						{
							has_not=true;
							values[i]=values[i].replace("NOT", "");
						}
						
						if(!has_not)
						{
							if(fuzzy==1)
							{
								bool_inner.must(QueryBuilders
										.fuzzyLikeThisQuery("object.title.value",
												"object.description.value")
										.fuzziness(Fuzziness.fromSimilarity((float) similarity))
										.likeText(values[i])
										.maxQueryTerms(2));
								
							}
							else
							{
								bool_inner.must(QueryBuilders
										.queryString(values[i])
										.field("object.title.value")
										.field("object.description.value"));
							}
						}
						else
						{
							if(fuzzy_not==1)
							{
								bool_inner.mustNot(QueryBuilders
										.fuzzyLikeThisQuery("object.title.value",
												"object.description.value")
										.fuzziness(Fuzziness.fromSimilarity((float) similarity))
										.likeText(values[i])
										.maxQueryTerms(2));
							}
							else
							{
								bool_inner.mustNot(QueryBuilders
										.queryString(values[i])
										.field("object.title.value")
										.field("object.description.value"));
							}
						}
						/*build_o.must(QueryBuilders
							.queryString(values[i])
							.field("object.title.value")
							.field("object.description.value")
							);*/
					}
					
					bool_q.should(bool_inner);
				}
				build_o.must(bool_q);
			}
			
			String entity_type=parser.parseEntityType(request);
			if(!entity_type.isEmpty())
			{
				search_parent=true;
				
				BoolQueryBuilder bool_q=QueryBuilders.boolQuery();
				String or_values[]=entity_type.split("OR");
				for(int i=0;i<or_values.length;i++)
				{
					String and_values[]=or_values[i].split("AND");
					BoolQueryBuilder bool_inner=QueryBuilders.boolQuery();
					
					for(int j=0;j<and_values.length;j++)
					{
						bool_inner.must(QueryBuilders.termQuery("object.type", and_values[j]));
					}
					bool_q.should(bool_inner);
				}
				build_o.must(bool_q);
				
				/*String values[]=entity_type.split("AND");
				
				for(int i=0;i<values.length;i++)
					build_o.must(QueryBuilders.termQuery("object.type", values[i]));*/
			}
			
			String type=parser.parseType(request);
			if(!type.isEmpty())
			{
				try {
					fuzzy = Integer.valueOf(config.getValue("fuzzy_type"));
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					fuzzy=1;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					fuzzy=1;
				}

				int fuzzy_not=0;

				try {
					fuzzy_not = Integer.valueOf(config.getValue("fuzzy_not"));
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					fuzzy_not=0;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					fuzzy_not=0;
				}
				
				
				BoolQueryBuilder bool_q=QueryBuilders.boolQuery();
				String or_values[]=type.split("OR");
				for(int i=0;i<or_values.length;i++)
				{
					String and_values[]=or_values[i].split("AND");
					BoolQueryBuilder bool_inner=QueryBuilders.boolQuery();
					
					for(int j=0;j<and_values.length;j++)
					{
						
						boolean has_not=false;
						
						if(and_values[j].contains("NOT"))
						{
							has_not=true;
							and_values[j]=and_values[j].replace("NOT", "");
						}
						
						if(!has_not)
						{
							if(fuzzy!=1)
								bool_inner.must(QueryBuilders.termQuery("type", and_values[j]));
							else
								bool_inner.must(QueryBuilders
									.fuzzyLikeThisQuery("type")
									.fuzziness(Fuzziness.fromSimilarity((float) similarity))
									.likeText(and_values[j])
									.maxQueryTerms(2)
									);
						}
						else
						{
							if(fuzzy_not!=1)
								bool_inner.mustNot(QueryBuilders.termQuery("type", and_values[j]));
							else
								bool_inner.mustNot(QueryBuilders
									.fuzzyLikeThisQuery("type")
									.fuzziness(Fuzziness.fromSimilarity((float) similarity))
									.likeText(and_values[j])
									.maxQueryTerms(2)
									);
						}
						
					}
					bool_q.should(bool_inner);
				}
				build_child.must(bool_q);
				
				
				/*String values[]=type.split("AND");
				
				for(int i=0;i<values.length;i++)
					filters.add(FilterBuilders.termFilter("type",values[i]));*/
			}
				//filters.add(FilterBuilders.termFilter("type",type));
				//build.must(QueryBuilders.matchQuery("type", type));

			String author=parser.parseAuthor(request);
			if(!author.isEmpty())
			{
				try {
					fuzzy = Integer.valueOf(config.getValue("fuzzy_author"));
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					fuzzy=1;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					fuzzy=1;
				}
				
				int fuzzy_not=0;

				try {
					fuzzy_not = Integer.valueOf(config.getValue("fuzzy_not"));
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					fuzzy_not=0;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					fuzzy_not=0;
				}
				
				BoolQueryBuilder bool_q=QueryBuilders.boolQuery();
				String or_values[]=author.split("OR");
				for(int i=0;i<or_values.length;i++)
				{
					String and_values[]=or_values[i].split("AND");
					BoolQueryBuilder bool_inner=QueryBuilders.boolQuery();
					
					for(int j=0;j<and_values.length;j++)
					{

						boolean has_not=false;
						
						if(and_values[j].contains("NOT"))
						{
							has_not=true;
							and_values[j]=and_values[j].replace("NOT", "");
						}
						
						if(!has_not)
						{
							if(fuzzy!=1)
								bool_inner.must(QueryBuilders.termQuery("creator.value", 
										and_values[j]));
							else
								bool_inner.must(QueryBuilders
									.fuzzyLikeThisQuery("creator.value")
									.fuzziness(Fuzziness.fromSimilarity((float) similarity))
									.likeText(and_values[j])
									.maxQueryTerms(25)
									);
						}
						else
						{
							if(fuzzy_not==0)
								bool_inner.mustNot(QueryBuilders.termQuery("creator.value", and_values[j]));
							else
								bool_inner.mustNot(QueryBuilders
									.fuzzyLikeThisQuery("creator.value")
									.fuzziness(Fuzziness.fromSimilarity((float) similarity))
									.likeText(and_values[j])
									.maxQueryTerms(2)
									);
						}
					}
					bool_q.should(bool_inner);
				}
				build_child.must(bool_q);
				
				
				/*String values[]=author.split("AND");
				
				for(int i=0;i<values.length;i++)
					filters.add(FilterBuilders.termFilter("creator.value",values[i]));*/
			}	
			
			String collection=parser.parseCollection(request);
			if(!collection.isEmpty())
			{				
				try {
					fuzzy = Integer.valueOf(config.getValue("fuzzy_collection"));
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					fuzzy=1;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					fuzzy=1;
				}

				int fuzzy_not=0;

				try {
					fuzzy_not = Integer.valueOf(config.getValue("fuzzy_not"));
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					fuzzy_not=0;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					fuzzy_not=0;
				}
				
				String or_values[]=collection.split("OR");
				BoolQueryBuilder bool_q=QueryBuilders.boolQuery();
				for(int j=0;j<or_values.length;j++)
				{
				
					String values[]=or_values[j].split("AND");
					BoolQueryBuilder bool_inner=QueryBuilders.boolQuery();
					
					for(int i=0;i<values.length;i++)
					{					
						BoolQueryBuilder bool_build =QueryBuilders.boolQuery();

						boolean has_not=false;
						
						if(values[i].contains("NOT"))
						{
							has_not=true;
							values[i]=values[i].replace("NOT", "");
						}
						
						
							if(fuzzy!=1)
									bool_build.must(QueryBuilders
												.queryString(values[i])
												.field("object.title.value")
											);
							else
									bool_build.must(QueryBuilders
											.fuzzyLikeThisQuery("object.title.value")
											.fuzziness(Fuzziness.fromSimilarity((float) similarity))
											.likeText(values[i])
											.maxQueryTerms(2)
										);
						
						bool_build.must(QueryBuilders
										.queryString("collection")
										.field("object.type")
									)
								;//.must(QueryBuilders.matchQuery("type","collection"));
											
							SearchResponse response=client.prepareSearch("cimmyt")
									.setTypes("object")
									//.setSearchType(SearchType.SCAN)
									//.setScroll(new TimeValue(60000))
									.setQuery(bool_build)
									.setSize(1)
									.execute()
									.actionGet();
							
						try
						{
							for(SearchHit hit : response.getHits().getHits())
							{
								/*build_child*/
								if(!has_not)
									bool_inner.must(
										QueryBuilders.termQuery("collection.id",hit.getId())
										);
								else
									bool_inner.mustNot(
											QueryBuilders.termQuery("collection.id",hit.getId())
											);
								break;
							}
							
						}
						catch(java.lang.ArrayIndexOutOfBoundsException e)
						{
							/*build_child*/
								bool_inner.must(
									QueryBuilders.termQuery("collection.id","998")
									);
							
						}
						
						if(response.getHits().getTotalHits()==0)
							/*build_child*/
								bool_inner.must(
									QueryBuilders.termQuery("collection.id","999")
									);
					}
					bool_q.should(bool_inner);
				}
				
				build_child.must(bool_q);
			}			
			
			String subject=parser.parseSubject(request);
			if(!subject.isEmpty())
			{
				search_parent=true;
				
				try {
					fuzzy = Integer.valueOf(config.getValue("fuzzy_subject"));
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					fuzzy=1;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					fuzzy=1;
				}

				int fuzzy_not=0;

				try {
					fuzzy_not = Integer.valueOf(config.getValue("fuzzy_not"));
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					fuzzy_not=0;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					fuzzy_not=0;
				}
				
				BoolQueryBuilder bool_q=QueryBuilders.boolQuery();
				String or_values[]=subject.split("OR");
				for(int i=0;i<or_values.length;i++)
				{
					String and_values[]=or_values[i].split("AND");
					BoolQueryBuilder bool_inner=QueryBuilders.boolQuery();
					
					for(int j=0;j<and_values.length;j++)
					{
						boolean has_not=false;
						
						if(and_values[j].contains("NOT"))
						{
							has_not=true;
							and_values[j]=and_values[j].replace("NOT", "");
						}
						
						if(!has_not)
						{
							if(fuzzy!=1)
								bool_inner.must(QueryBuilders.termQuery("subject.value", and_values[j]));
							else
								bool_inner.must(QueryBuilders
									.fuzzyLikeThisQuery("subject.value")
									.fuzziness(Fuzziness.fromSimilarity((float) similarity))
									.likeText(and_values[j])
									.maxQueryTerms(2)
									);
						}
						else
						{
							if(fuzzy_not!=1)
								bool_inner.mustNot(QueryBuilders.termQuery("subject.value", and_values[j]));
							else
								bool_inner.mustNot(QueryBuilders
									.fuzzyLikeThisQuery("subject.value")
									.fuzziness(Fuzziness.fromSimilarity((float) similarity))
									.likeText(and_values[j])
									.maxQueryTerms(2)
									);
						}
					}
					bool_q.should(bool_inner);
				}
				build_o.must(bool_q);
				
				/*
				String values[]=subject.split("AND");
				
				for(int i=0;i<values.length;i++)
					build_o.must(QueryBuilders.termQuery("subject.value", values[i]));*/
			}
				//build_o.must(QueryBuilders.matchQuery("subject.value", subject));
			
			String lang=parser.parseLanguage(request);
			if(!lang.isEmpty())
			{
				search_parent=true;
				
				BoolQueryBuilder bool_q=QueryBuilders.boolQuery();
				String or_values[]=lang.split("OR");
				for(int i=0;i<or_values.length;i++)
				{
					String and_values[]=or_values[i].split("AND");
					BoolQueryBuilder bool_inner=QueryBuilders.boolQuery();
					
					for(int j=0;j<and_values.length;j++)
					{
						bool_inner.must(QueryBuilders.termQuery("language.value", and_values[j]));
					}
					bool_q.should(bool_inner);
				}
				build_o.must(bool_q);
				
				/*String values[]=lang.split("AND");
				
				for(int i=0;i<values.length;i++)
					build_o.must(QueryBuilders.termQuery("language.value", values[i]));*/
			}
				//build_o.must(QueryBuilders.matchQuery("language.value", lang));
			
			String location=parser.parseLocation(request);
			if(!location.isEmpty())
			{
				try {
					fuzzy = Integer.valueOf(config.getValue("fuzzy_location"));
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					fuzzy=1;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					fuzzy=1;
				}

				int fuzzy_not=0;

				try {
					fuzzy_not = Integer.valueOf(config.getValue("fuzzy_not"));
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					fuzzy_not=0;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					fuzzy_not=0;
				}
				
				BoolQueryBuilder bool_q=QueryBuilders.boolQuery();
				String or_values[]=location.split("OR");
				for(int i=0;i<or_values.length;i++)
				{
					String and_values[]=or_values[i].split("AND");
					BoolQueryBuilder bool_inner=QueryBuilders.boolQuery();
					
					for(int j=0;j<and_values.length;j++)
					{
						boolean has_not=false;
						
						if(and_values[j].contains("NOT"))
						{
							has_not=true;
							and_values[j]=and_values[j].replace("NOT", "");
						}

						if(!has_not)
						{
							if(fuzzy!=1)
								bool_inner.must(QueryBuilders.termQuery("location.value", and_values[j]));
							else
								bool_inner.must(QueryBuilders
									.fuzzyLikeThisQuery("location.value")
									.fuzziness(Fuzziness.fromSimilarity((float) similarity))
									.likeText(and_values[j])
									.maxQueryTerms(2)
									);
						}
						else
						{
							if(fuzzy_not!=1)
								bool_inner.mustNot(QueryBuilders.termQuery("location.value", and_values[j]));
							else
								bool_inner.mustNot(QueryBuilders
									.fuzzyLikeThisQuery("location.value")
									.fuzziness(Fuzziness.fromSimilarity((float) similarity))
									.likeText(and_values[j])
									.maxQueryTerms(2)
									);
						}
					}
					bool_q.should(bool_inner);
				}
				build_child.must(bool_q);
				
				
				/*String values[]=location.split("AND");
				
				for(int i=0;i<values.length;i++)
					filters.add(FilterBuilders.termFilter("location.value",values[i]));*/
			}
				//filters.add(FilterBuilders.termFilter("location.value",location));
				//build.must(QueryBuilders.matchQuery("location.value", location));
			
			String relation=parser.parseRelation(request);
			if(!relation.isEmpty())
			{
				try {
					fuzzy = Integer.valueOf(config.getValue("fuzzy_relation"));
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					fuzzy=1;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					fuzzy=1;
				}
				
				BoolQueryBuilder bool_q=QueryBuilders.boolQuery();
				String or_values[]=relation.split("OR");
				for(int i=0;i<or_values.length;i++)
				{
					String and_values[]=or_values[i].split("AND");
					BoolQueryBuilder bool_inner=QueryBuilders.boolQuery();
					
					for(int j=0;j<and_values.length;j++)
					{
						if(fuzzy==1)
							bool_inner.must(QueryBuilders
								.fuzzyLikeThisQuery("relation")
								.fuzziness(Fuzziness.fromSimilarity((float) similarity))
								.likeText(and_values[j])
								.maxQueryTerms(2)
								);
						else
							bool_inner.must(QueryBuilders.termQuery("relation", and_values[j]));
					}
					bool_q.should(bool_inner);
				}
				build_o.must(bool_q);
				
				
				/*String values[]=relation.split("AND");
				
				for(int i=0;i<values.length;i++)
					filters.add(FilterBuilders.termFilter("relation",values[i]));*/
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
				
				build_child.must(
						QueryBuilders
						.rangeQuery("date")
						.gte(from_date)
						.lte(to_date)
						);	
					
				/*filters.add(FilterBuilders
						.rangeFilter("date")
						.gte(from_date)
						.lte(to_date));*/
			}
			  
			BuildSearchResponse builder=new BuildSearchResponse();
			//results=builder.buildFrom(client,build_o,filters,page,search_parent);
			
			results=builder.buildFrom_beta(client,build_o,build_child,
					page,search_parent, build_enhanced);

		client.close();
		
		String format;
		//ParseGET parser=new ParseGET();
		format=parser.parseFormat(request);
		if(format.equals("xml"))
		{
			ToXML converter=new ToXML();
			results=converter.convertToXMLFreeText(results);
		}
		
		//results="";
		return results;
	    
	}
}
