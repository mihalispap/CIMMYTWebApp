package com.agroknow.cimmyt.utils;

import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.SearchHit;
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
						entry.getTerm().string().equals("organization"))
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
				FacetBuilders.termsFacet("entity_types").field("_type");
		TermsFacetBuilder facetLangs =
				FacetBuilders.termsFacet("langs").field("language.value");
		TermsFacetBuilder facetTypes =
				FacetBuilders.termsFacet("type").field("type");
		
		SearchResponse response=client.prepareSearch("cimmyt")
				//.setTypes("object","resource",
				//		"person","organization",
				//		"dataset_software","collection")
				.setQuery(build)
				.setFrom(page*page_size)
				.setSize(page_size)
				.addFacet(facetET)
				.addFacet(facetLangs)
				.addFacet(facetTypes)
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
				+ ",\"results\":[";
		
		for(SearchHit hit : response.getHits().getHits())
			result+=hit.getSourceAsString()+",";
		
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
