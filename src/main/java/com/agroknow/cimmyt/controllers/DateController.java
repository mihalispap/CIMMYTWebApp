package com.agroknow.cimmyt.controllers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

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
import org.elasticsearch.index.query.FilterBuilders;
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
public class DateController {

	@ApiOperation(value = "Find objects on creation date")
	@RequestMapping( value="/date", method={RequestMethod.GET})
	@ApiImplicitParams({
		@ApiImplicitParam(
    			name = "from", 
    			value = "entity created after this date", 
    			required = false, 
    			dataType = "date", 
    			paramType = "query", 
    			defaultValue="2012"),
        @ApiImplicitParam(
        			name = "to", 
        			value = "entity created prior to this date", 
        			required = false, 
        			dataType = "date", 
        			paramType = "query", 
        			defaultValue="2016")
      })
    String run(HttpServletRequest request) { 
		Settings settings = ImmutableSettings.settingsBuilder()
		        .put("cluster.name", "agroknow").build();
		
		Client client = new TransportClient(settings)
		        .addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
		
		ParseGET parser=new ParseGET();
		String fromDate=parser.parseFromDate(request);
		String toDate=parser.parseToDate(request);
		
		if(fromDate.isEmpty() && toDate.isEmpty())
			return "{\"total\":0"+
					",\"results\":[]}";
		
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar=new GregorianCalendar();
		
		
		if(fromDate.isEmpty())
			fromDate="500-01-01";
		if(toDate.isEmpty())
			toDate=sdf.format(calendar.getTime());
		
		String results="";
		
		QueryBuilder query=
				QueryBuilders
					.rangeQuery("created")
					.from(fromDate)
					.to(toDate)
					.includeLower(true)
					.includeUpper(true);
					
		
		SearchResponse response=client.prepareSearch("cimmyt")
				.setTypes("object")
				.setSearchType(SearchType.SCAN)
				.setScroll(new TimeValue(60000))
				.setQuery(query)
				.setExplain(true)
				.execute().actionGet();
	
			BuildSearchResponse builder=new BuildSearchResponse();
			results=builder.buildFrom(client,response);
		
		client.close();
		
		//results="";
		return results;
	    
	}
}
