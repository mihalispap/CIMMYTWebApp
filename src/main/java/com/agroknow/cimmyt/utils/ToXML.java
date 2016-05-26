package com.agroknow.cimmyt.utils;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ToXML 
{
	public String convertToXMLID(String json_input)
	{
		String xml="";
		try
		{
			JSONObject json = null;
			
			json = (JSONObject)new JSONParser().parse(json_input);

	
			xml+="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "	<result "
					+ "xmlns:dcterms=\"http://purl.org/dc/terms/\""+
						"xmlns:oa=\"http://www.openannotation.org/spec/core\""+
						"xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\""
					+ ">";
			xml+="<total>"+json.get("total")+"</total>";
			
			
			JSONArray jresults=(JSONArray)json.get("results");
			Iterator<JSONObject> iterator=jresults.iterator();
			
			while(iterator.hasNext())
			{
				JSONObject obj=iterator.next();
				
				Set set=obj.entrySet();
				
				Iterator keys_iterator=set.iterator();
				while(keys_iterator.hasNext())
				{
					Map.Entry entry=(Map.Entry)keys_iterator.next();
					
					if(entry.getKey().toString().equals("object"))
					{
						xml+="<object>";
						xml+=convertObject(entry.getValue().toString());
						xml+="</object>";
					}
					if(entry.getKey().toString().equals("detailed"))
					{
						xml+="<detailed>";
						//xml+=convertDetailed(entry.getValue());
						xml+="</detailed>";
					}
					System.out.println(entry.getKey()+":"+entry.getValue());
				}

				
				//System.out.println("Key values?:"+iterator.next().keySet());
				
				xml+="</result>";
			}		
		}
		catch(java.lang.Exception e)
		{
			e.printStackTrace();
		}
		
		
		return xml;
	}
	
	public String convertObject(String json_input)
	{
		String xml="";
		try
		{
			JSONObject json = null;
			
			json = (JSONObject)new JSONParser().parse(json_input);
			Set set=json.entrySet();
			
			Iterator keys_iterator=set.iterator();
			while(keys_iterator.hasNext())
			{
				Map.Entry entry=(Map.Entry)keys_iterator.next();

				if(entry.getKey().toString().equals("title"))
				{
					try
					{
						JSONArray jresults=(JSONArray)json.get("title");
						Iterator<JSONObject> iterator=jresults.iterator();
						
						while(iterator.hasNext())
						{
							JSONObject obj=iterator.next();
							xml+=parseTitle(obj);							
						}
					}
					catch(java.lang.ClassCastException e)
					{
						JSONObject obj=(JSONObject)json.get("title");
						
						xml+=parseTitle(obj);
					}
				}

				if(entry.getKey().toString().equals("subject"))
				{
					try
					{
						JSONArray jresults=(JSONArray)json.get("subject");
						Iterator<JSONObject> iterator=jresults.iterator();
						
						while(iterator.hasNext())
						{
							JSONObject obj=iterator.next();
							xml+=parseSubject(obj);							
						}
					}
					catch(java.lang.ClassCastException e)
					{
						e.printStackTrace();
						
						JSONObject obj=(JSONObject)json.get("subject");
						
						xml+=parseSubject(obj);
					}
				}

				if(entry.getKey().toString().equals("description"))
				{
					try
					{
						JSONArray jresults=(JSONArray)json.get("description");
						Iterator<JSONObject> iterator=jresults.iterator();
						
						while(iterator.hasNext())
						{
							JSONObject obj=iterator.next();
							xml+=parseDescription(obj);							
						}
					}
					catch(java.lang.ClassCastException e)
					{
						JSONObject obj=(JSONObject)json.get("description");
						
						xml+=parseDescription(obj);
					}
				}

				if(entry.getKey().toString().equals("updated"))
				{
					try
					{
						JSONArray jresults=(JSONArray)json.get("updated");
						Iterator<String> iterator=jresults.iterator();
						
						while(iterator.hasNext())
						{
							String obj=iterator.next();
							xml+=parseDate(obj);		
						}
					}
					catch(java.lang.ClassCastException e)
					{
						String obj=(String)json.get("updated");
						
						xml+=parseDate(obj);
					}
				}
				
				if(entry.getKey().toString().equals("created"))
				{
					try
					{
						JSONArray jresults=(JSONArray)json.get("created");
						Iterator<String> iterator=jresults.iterator();
						
						while(iterator.hasNext())
						{
							String obj=iterator.next();
							xml+=parseCreated(obj);		
						}
					}
					catch(java.lang.ClassCastException e)
					{
						String obj=(String)json.get("created");
						
						xml+=parseCreated(obj);
					}
				}
				
				System.out.println(entry.getKey()+"|||"+entry.getValue());
			}
			
		}
		catch(java.lang.Exception e)
		{
			e.printStackTrace();
		}
	
		return xml;
	}
	
	public String parseTitle(JSONObject obj)
	{
		String xml="";
		
		String lang="", title="";
		lang=obj.get("lang").toString();
		title=obj.get("value").toString();
		
		xml+="<dc:title";
		
		if(!lang.isEmpty())
			xml+=" xml:lang=\""+lang+"\"";
		
		xml+="><![CDATA[";

		xml+=title+"]]></dc:title>";
		
		return xml;
		
	}

	public String parseDescription(JSONObject obj)
	{
		String xml="";
		
		String lang="", title="";
		lang=obj.get("lang").toString();
		title=obj.get("value").toString();
		
		xml+="<dc:description";
		
		if(!lang.isEmpty())
			xml+=" xml:lang=\""+lang+"\"";
		
		xml+="><![CDATA[";

		xml+=title+"]]></dc:description>";
		
		return xml;
		
	}

	public String parseSubject(JSONObject obj)
	{
		String xml="";
		
		String value="";
		String voc="";
		String uri="";
		String score="";
		
		value=obj.get("value").toString();

		xml+="<dc:subject";
		try
		{
			JSONArray jvocs=(JSONArray)obj.get("vocabulary");
			Iterator<String> iterator=jvocs.iterator();
			
			while(iterator.hasNext())
			{
				String v=iterator.next();
				
				if(!v.isEmpty())
					xml+=" vocabulary=\""+v+"\"";		
			}
		}
		catch(java.lang.ClassCastException e)
		{
			voc=obj.get("vocabulary").toString();
			if(!voc.isEmpty())
				xml+=" vocabulary=\""+voc+"\"";
		}
		
		try
		{
			JSONArray jvocs=(JSONArray)obj.get("score");
			Iterator<String> iterator=jvocs.iterator();
			
			while(iterator.hasNext())
			{
				String v=String.valueOf(iterator.next());
				
				if(!v.isEmpty())
					xml+=" score=\""+v+"\"";		
			}
		}
		catch(java.lang.ClassCastException e)
		{
			//e.printStackTrace();
			score=obj.get("score").toString();
			if(!score.isEmpty())
				xml+=" score=\""+score+"\"";
		}	
		
		try
		{
			JSONArray jvocs=(JSONArray)obj.get("uri");
			Iterator<String> iterator=jvocs.iterator();
			
			while(iterator.hasNext())
			{
				String v=iterator.next();
				
				if(!v.isEmpty())
					xml+=" rdf:resource=\""+v+"\"";		
			}
		}
		catch(java.lang.ClassCastException e)
		{
			uri=obj.get("uri").toString();
			if(!uri.isEmpty())
				xml+=" rdf:resource=\""+voc+"\"";
		}		
		
		
		xml+="><![CDATA[";

		xml+=value+"]]></dc:subject>";
		
		return xml;
		
	}


	public String parseDate(String obj)
	{
		String xml="";
		
		xml+="<dc:date";
		xml+="><![CDATA[";
		xml+=obj+"]]></dc:date>";
		
		return xml;
		
	}

	public String parseCreated(String obj)
	{
		String xml="";
		
		xml+="<dcterms:created";
		xml+="><![CDATA[";
		xml+=obj+"]]></dcterms:created>";
		
		return xml;
		
	}
	
}






