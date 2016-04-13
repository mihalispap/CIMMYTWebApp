package com.agroknow.cimmyt;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;

@Configuration
public class SpringElasticsearchExample {
	

	@Autowired
	private ElasticsearchTemplate template;

	public static void run(String[] args) throws URISyntaxException, Exception {
		//ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		
		Settings settings = ImmutableSettings.settingsBuilder()
		        .put("cluster.name", "cimmyt").build();
		
		Client client = new TransportClient(settings)
		        .addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
		        //.addTransportAddress(new InetSocketTransportAddress("host2", 9300));
		System.out.println("Status:"+client.settings().toString());
		// on shutdown
		
		GetResponse response = client.prepareGet("cimmyt", "object", "1403840553")
		        .execute()
		        .actionGet();

		System.out.println("Response:"+response.getSourceAsString());
		
		client.close();

		//ctx.close();
	}

}
