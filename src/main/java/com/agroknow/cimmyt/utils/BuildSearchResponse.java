package com.agroknow.cimmyt.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.facet.Facet;
import org.elasticsearch.search.facet.FacetBuilders;
import org.elasticsearch.search.facet.terms.TermsFacet;
import org.elasticsearch.search.facet.terms.TermsFacetBuilder;

public class BuildSearchResponse {

	private int page_size=10;
	
	public String buildFrom(Client client, TermsFacet f, SearchResponse response, String facet)
	{
		String result="";
		
		//while(true)
		//{
			TermsFacet fac=(TermsFacet) response.getFacets()
					.facet(facet);
					//.facets().get(0);
			
			int size=0;
			for(TermsFacet.Entry entry : fac)
			{
				if(entry.getTerm().string().equals("") || 
						entry.getTerm().string().isEmpty() ||
						entry.getTerm().string()=="")
					continue;
				
				result+="{\"value\":\""+entry.getTerm()+"\",\"count\":"+entry.getCount()+"},";
				size++;
			}
			
			result="{\"total\":"+size+
					",\"facet_name\":"+f.getName()+
			",\"results\":["+result;
			
			/*response=client.prepareSearchScroll(response.getScrollId())
					.setScroll(new TimeValue(60000))
					.execute()
					.actionGet();
			
			if(response.getHits().getHits().length==0)
					break;
		}*/
		
		result+="]}";
		result=result.replace(",]}", "]}");
		
		return result;
	}
	
	public String buildFrom(Client client, SearchResponse response, String facet)
	{
		String result="";
		
		//while(true)
		//{
			TermsFacet fac=(TermsFacet) response.getFacets()
					.facet(facet);
					//.facets().get(0);
			
			int size=0;
			for(TermsFacet.Entry entry : fac)
			{
				if(entry.getTerm().string().equals("") || 
						entry.getTerm().string().isEmpty() ||
						entry.getTerm().string()=="")
					continue;
				
				if(entry.getTerm().string().equals("object"))
						continue;
				
				if(facet.equals("type"))
				{
					if(entry.getTerm().string().equals("resource")
							||
						entry.getTerm().string().equals("dataset_software")
							||
						entry.getTerm().string().equals("person")
						||
						entry.getTerm().string().equals("organization")
						||
						entry.getTerm().string().equals("collection"))
							continue;
				}
				
				result+="{\"value\":\""+entry.getTerm()+"\",\"count\":"+entry.getCount()+"},";
				size++;
			}
			
			result="{\"total\":"+size+
					",\"facet_name\":"+facet+
			",\"results\":["+result;
			
			/*response=client.prepareSearchScroll(response.getScrollId())
					.setScroll(new TimeValue(60000))
					.execute()
					.actionGet();
			
			if(response.getHits().getHits().length==0)
					break;
		}*/
		
		result+="]}";
		result=result.replace(",]}", "]}");
		
		return result;
	}

	public String buildFrom(Client client, SearchResponse response)
	{
		String result="{\"total\":"+response.getHits().getTotalHits()+
				",\"results\":[";
		
		
		while(true)
		{
			for(SearchHit hit : response.getHits().getHits())
				result+=hit.getSourceAsString()+",";
			//if(true)
			//	break;
			response=client.prepareSearchScroll(response.getScrollId())
					.setScroll(new TimeValue(60000))
					.execute()
					.actionGet();
			
			if(response.getHits().getHits().length==0)
					break;
		}
			
		result+="]}";
		result=result.replace(",]}", "]}");
		
		return result;
	}

	public String buildFrom(Client client, BoolQueryBuilder build, int page)
	{
		TermsFacetBuilder facet =
				FacetBuilders.termsFacet("subjects").field("creator.value").size(9999);
		
		/*SearchResponse response2=client.prepareSearch("cimmyt")
		.setTypes("resource",
				"person","organization",
				"dataset_software","collection","object")
		.setQuery(build)
		//.setQuery(query)
		.setFrom(page*page_size)
		.setSize(page_size)
		.addFacet(facet)
		.execute()
		.actionGet();*/
		
		
		SearchResponse response;/*=client.prepareSearch("cimmyt")
				.setTypes("resource",
						"person","organization",
						"dataset_software","collection","object")
				.setQuery(build)
				//.setQuery(query)
				.setFrom(page*page_size)
				.setSize(page_size)
				.addFacet(facetET)
				.addFacet(facetLangs)
				.addFacet(facetTypes)
				.addFacet(facetAuthors)
				.execute()
				.actionGet();*/
		

		response = client.prepareSearch("cimmyt")
				.setTypes("person", "organization","object", 
						"resource", "dataset_software")
				.setQuery(build)
                .addAggregation(AggregationBuilders.terms("authors").field("creator.value")
                		.size(0).order(Terms.Order.count(false)))
                .addAggregation(AggregationBuilders.terms("types").field("type")
                		.size(9999).order(Terms.Order.count(false)))
                .addAggregation(AggregationBuilders.terms("langs").field("language.value")
                		.size(9999).order(Terms.Order.count(false)))
                .addAggregation(AggregationBuilders.terms("locations").field("location.value")
                		.size(9999).order(Terms.Order.count(false)))
                .addAggregation(AggregationBuilders.terms("relations").field("relation")
                		.size(9999).order(Terms.Order.count(false)))
                .addAggregation(AggregationBuilders.terms("subjects").field("subject.value")
                		.size(9999).order(Terms.Order.count(false)))
                .addAggregation(AggregationBuilders.terms("entity-types").field("object.type")
                		.size(9999).order(Terms.Order.count(false)))
                //.setFrom(page*page_size)
				//.setSize(page_size)
                .execute()
                .actionGet();
		
		
		//result+=buildFacets(response);

		String facet_name="entity_types";
		
		String result="{\"total\":"+response.getHits().getTotalHits()
				+",\"page\":"+page
				+",\"page_size\":"+page_size
				+",\"time_elapsed\":"+(double)response.getTookInMillis()/1000
				+",\"facets\":[{"+buildFacet(response, "entity-types")+"}]"
				+",\"facets\":[{"+buildFacet(response, "types")+"}]"
				+",\"facets\":[{"+buildFacet(response, "langs")+"}]"
				+",\"facets\":[{"+buildFacet(response, "authors")+"}]"
				+",\"facets\":[{"+buildFacet(response, "locations")+"}]"
				+",\"facets\":[{"+buildFacet(response, "relations")+"}]"
				+",\"facets\":[{"+buildFacet(response, "subjects")+"}]"
				//+",\"facetsBETA\":[{"+buildFrom(client,response2, "subjects")+"}]"
				+ ",\"results\":[";
		
		
		
		for(SearchHit hit : response.getHits().getHits())
		{
			//if(1==1)
			//	break;
			String id=hit.getId();
			
			GetResponse specific = client.prepareGet("cimmyt", "object", id)
			        .execute()
			        .actionGet();
			result+=specific.getSourceAsString();
			
			//result+=hit.getSourceAsString()+",";
		}
		
			
		result+="]}";
		result=result.replace(",]}", "]}");
		
		result+=build.toString();

		//client.close();
		return result;
	}


public String buildFrom(Client client, BoolQueryBuilder build_object, BoolQueryBuilder build_resource
		, int page)
	{
		SearchResponse response_o = client.prepareSearch("cimmyt")
				.setTypes("person", "organization","object", 
						"resource", "dataset_software")
				.setQuery(build_object)
				.execute()
				.actionGet();
	
		SearchResponse response_r = client.prepareSearch("cimmyt")
				.setTypes("person", "organization","object", 
						"resource", "dataset_software")
				.setQuery(build_resource)
				.execute()
				.actionGet();
		
		//String ids_o
		
		SearchRequestBuilder response_object = client.prepareSearch("cimmyt")
				.setTypes("object")
				.setQuery(build_object);

		SearchRequestBuilder response_resource = client.prepareSearch("cimmyt")
				.setTypes("resource","dataset_software")
				.setQuery(build_resource);
		
		MultiSearchResponse response=client.prepareMultiSearch()
				.add(response_object)
				.add(response_resource)
				.execute()
				.actionGet();
		
		String result="";
		
		for(int i=0;i<response.getResponses().length;i++)
			result+=response.getResponses()[i].toString()+",";
		
		result+="ASDASDSA"+response.contextSize();
		result+="<br>"+build_object.toString();
		result+="<br>"+response.toString();
                /*.addAggregation(AggregationBuilders.terms("langs").field("language.value")
                		.size(9999).order(Terms.Order.count(false)))
                .addAggregation(AggregationBuilders.terms("locations").field("location.value")
                		.size(9999).order(Terms.Order.count(false)))
                .addAggregation(AggregationBuilders.terms("relations").field("relation")
                		.size(9999).order(Terms.Order.count(false)))
                .addAggregation(AggregationBuilders.terms("subjects").field("subject.value")
                		.size(9999).order(Terms.Order.count(false)))
                .addAggregation(AggregationBuilders.terms("entity-types").field("object.type")
                		.size(9999).order(Terms.Order.count(false)))
                //.setFrom(page*page_size)
				//.setSize(page_size)
                .execute()
                .actionGet();*/
		
		
		//result+=buildFacets(response);
		/*
		String facet_name="entity_types";
		
		String result="{\"total\":"+response.getHits().getTotalHits()
				+",\"page\":"+page
				+",\"page_size\":"+page_size
				+",\"time_elapsed\":"+(double)response.getTookInMillis()/1000
				+",\"facets\":[{"+buildFacet(response, "entity-types")+"}]"
				+",\"facets\":[{"+buildFacet(response, "types")+"}]"
				+",\"facets\":[{"+buildFacet(response, "langs")+"}]"
				+",\"facets\":[{"+buildFacet(response, "authors")+"}]"
				+",\"facets\":[{"+buildFacet(response, "locations")+"}]"
				+",\"facets\":[{"+buildFacet(response, "relations")+"}]"
				+",\"facets\":[{"+buildFacet(response, "subjects")+"}]"
				//+",\"facetsBETA\":[{"+buildFrom(client,response2, "subjects")+"}]"
				+ ",\"results\":[";
		
		
		
		for(SearchHit hit : response.getHits().getHits())
		{
			//if(1==1)
			//	break;
			String id=hit.getId();
			
			GetResponse specific = client.prepareGet("cimmyt", "object", id)
			        .execute()
			        .actionGet();
			result+=specific.getSourceAsString();
			
			//result+=hit.getSourceAsString()+",";
		}
		
			
		result+="]}";
		result=result.replace(",]}", "]}");
		
		result+=build.toString();
		 */
		//client.close();
		return result;
	}

	
	public String buildFacet(SearchResponse response, String facet_name)
	{
		Terms  terms = response.getAggregations().get(facet_name);
		List<Bucket> bucketList=new ArrayList<Bucket>();

		int size=0;
		bucketList=terms.getBuckets();
		String fValue="";
		for(int i=0;i<bucketList.size();i++)
		{
			
			if(bucketList.get(i).getKey().equals("") || 
					bucketList.get(i).getKey().isEmpty() ||
					bucketList.get(i).getKey()=="")
				continue;
			
			if(bucketList.get(i).getKey().equals("object"))
					continue;
			
			if(facet_name.equals("types"))
			{
				if(bucketList.get(i).getKey().equals("resource")
						||
						bucketList.get(i).getKey().equals("dataset_software")
						||
						bucketList.get(i).getKey().equals("person")
					||
					bucketList.get(i).getKey().equals("organization")
					||
					bucketList.get(i).getKey().equals("collection"))
						continue;
			}
						
			fValue+="{\"value\":\""+bucketList.get(i).getKey()+"\", \"count\":"+
					bucketList.get(i).getDocCount()+"},";
			size++;
		}
		

		String result="{\"total\":"+size+
				",\"facet_name\":"+facet_name+
		",\"results\":["+fValue;
		
		result+="]}";
		result=result.replace(",]}", "]}");
		
		return result;
	}
	
	public String buildFrom(Client client, MultiSearchResponse response)
	{
		String result="{\"total\":"+response.getResponses().length+
				",\"results\":[";
		
		for(MultiSearchResponse.Item item : response.getResponses())
		{
			SearchResponse rsp=item.getResponse();
			while(true)
			{
				for(SearchHit hit : rsp.getHits().getHits())
					result+=hit.getSourceAsString()+",";
				
				rsp=client.prepareSearchScroll(rsp.getScrollId())
						.setScroll(new TimeValue(60000))
						.execute()
						.actionGet();
				
				if(rsp.getHits().getHits().length==0)
						break;
			}
		}
		
		result+="]}";
		result=result.replace(",]}", "]}");
		
		return result;
	}
	
}
