package com.agroknow.cimmyt.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.MultiSearchResponse;
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
		TermsFacetBuilder facetET =
				FacetBuilders.termsFacet("entity_types").field("object.type");
		TermsFacetBuilder facetLangs =
				FacetBuilders.termsFacet("langs").field("object.language.value");
		TermsFacetBuilder facetTypes =
				FacetBuilders.termsFacet("type").field("dataset_software.type");
		TermsFacetBuilder facetAuthors =
				FacetBuilders.termsFacet("author").field("creator.value");
		
		SearchResponse response=client.prepareSearch("cimmyt")
				/*.setTypes("object","resource",
						"person","organization",
						"dataset_software","collection")*/
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
				.actionGet();
		

		String facet_name="entity_types";
		
		String result="{\"total\":"+response.getHits().getTotalHits()
				+",\"page\":"+page
				+",\"page_size\":"+page_size
				+",\"time_elapsed\":"+(double)response.getTookInMillis()/1000
				+",\"facets\":[{"+buildFrom(client,response, "entity_types")+"}]"
				+",\"facets\":[{"+buildFrom(client,response, "type")+"}]"
				+",\"facets\":[{"+buildFrom(client,response, "langs")+"}]"
				+",\"facets\":[{"+buildFrom(client,response, "author")+"}]"
				+ ",\"results\":[";
		
		
		
		for(SearchHit hit : response.getHits().getHits())
		{
			String id=hit.getId();
			
			GetResponse specific = client.prepareGet("cimmyt", "object", id)
			        .execute()
			        .actionGet();
			result+=specific.getSourceAsString();
			
			//result+=hit.getSourceAsString()+",";
		}
		
		/*while(true)
		{
			for(SearchHit hit : response.getHits().getHits())
				result+=hit.getSourceAsString()+",";
			//if(true)
			//break;
			response=client.prepareSearchScroll(response.getScrollId())
					.setScroll(new TimeValue(60000))
					.execute()
					.actionGet();
			
			if(response.getHits().getHits().length==0)
					break;
		}*/
			
		result+="]}";
		result=result.replace(",]}", "]}");
		
		//result=response.toString();
		result+=build.getClass().getSimpleName()
				+"|"+build.getClass().toString()+"|"+
				build.toString();
		
		response = client.prepareSearch("cimmyt")
				.setTypes("resource",
						"person","organization",
						"dataset_software","collection","object")
				.setQuery(build)
                .addAggregation(AggregationBuilders.terms("keys").field("creator.value")
                		.size(9999).order(Terms.Order.count(false)))
                .execute().actionGet();

		Terms  terms = response.getAggregations().get("keys");
		List<Bucket> bucketList=new ArrayList<Bucket>();

		bucketList=terms.getBuckets();
		String fValue="";
		for(int i=0;i<bucketList.size();i++)
		{
			fValue+="{value:"+bucketList.get(i).getKey()+", count:"+
					bucketList.get(i).getDocCount()+"}";
		}
		
		//assertThat(buckets.size(), equalTo(3));
		result+=fValue;

		client.close();
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
