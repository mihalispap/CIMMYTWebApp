package com.agroknow.cimmyt.utils;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

//import org.apache.commons.lang.StringUtils;

public class ParseGET {

	public String parseAll(HttpServletRequest request)
	{

		Enumeration<String> params=request.getParameterNames();
		String param="", param_value="";
		
		String title="";
		String type="";
		
		while(params.hasMoreElements())
		{
			param=params.nextElement();
			param_value=request.getParameter(param);
			
			if(param.equalsIgnoreCase("title"))
				//title=StringUtils.trim(param_value);
				title=param_value;
			else if(param.equalsIgnoreCase("type"))
				//type=StringUtils.trim(param_value);
				type=param_value;
		}
		
		return "";
	}

	public String parseTitle(HttpServletRequest request)
	{
		Enumeration<String> params=request.getParameterNames();
		String param="", param_value="";
		
		String title="";
		
		while(params.hasMoreElements())
		{
			param=params.nextElement();
			param_value=request.getParameter(param);
			
			if(param.equalsIgnoreCase("title"))
			{
				//title=StringUtils.trim(param_value);
				return param_value;
			}
		}
		
		return "";
		
	}
	public String parseSubject(HttpServletRequest request)
	{
		Enumeration<String> params=request.getParameterNames();
		String param="", param_value="";
		
		String title="";
		
		while(params.hasMoreElements())
		{
			param=params.nextElement();
			param_value=request.getParameter(param);
			
			if(param.equalsIgnoreCase("subject"))
			{
				//title=StringUtils.trim(param_value);
				return param_value;
			}
		}
		
		return "";
		
	}
	public String parseType(HttpServletRequest request)
	{
		Enumeration<String> params=request.getParameterNames();
		String param="", param_value="";
		
		String type="";
		
		while(params.hasMoreElements())
		{
			param=params.nextElement();
			param_value=request.getParameter(param);
			
			if(param.equalsIgnoreCase("type"))
			{
				//type=StringUtils.trim(param_value);
				return param_value;
			}
		}
		
		return "";
		
	}
}
