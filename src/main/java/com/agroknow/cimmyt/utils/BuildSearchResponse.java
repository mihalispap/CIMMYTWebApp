package com.agroknow.cimmyt.utils;

import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.facet.terms.TermsFacet;

public class BuildSearchResponse {

	
	public String buildFrom(Client client, TermsFacet f, SearchResponse response, String facet)
	{
		String result="";
		
		//while(true)
		//{
			TermsFacet fac=(TermsFacet) response.getFacets()
					.facets().get(0);
					//.facetsAsMap().get(facet);
			
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
