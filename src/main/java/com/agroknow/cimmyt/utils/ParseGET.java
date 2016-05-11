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

	public String parseEntityType(HttpServletRequest request)
	{
		Enumeration<String> params=request.getParameterNames();
		String param="", param_value="";
		
		String title="";
		
		while(params.hasMoreElements())
		{
			param=params.nextElement();
			param_value=request.getParameter(param);
			
			if(param.equalsIgnoreCase("entity-type"))
			{
				//title=StringUtils.trim(param_value);
				return param_value;
			}
		}
		
		return "";
		
	}

	public String parseCollection(HttpServletRequest request)
	{
		Enumeration<String> params=request.getParameterNames();
		String param="", param_value="";
		
		String title="";
		
		while(params.hasMoreElements())
		{
			param=params.nextElement();
			param_value=request.getParameter(param);
			
			if(param.equalsIgnoreCase("collection"))
			{
				//title=StringUtils.trim(param_value);
				return param_value;
			}
		}
		
		return "";
		
	}

	public String parseAuthor(HttpServletRequest request)
	{
		Enumeration<String> params=request.getParameterNames();
		String param="", param_value="";
		
		String title="";
		
		while(params.hasMoreElements())
		{
			param=params.nextElement();
			param_value=request.getParameter(param);
			
			if(param.equalsIgnoreCase("author"))
			{
				//title=StringUtils.trim(param_value);
				return param_value;
			}
		}
		
		return "";
		
	}

	public String parseLanguage(HttpServletRequest request)
	{
		Enumeration<String> params=request.getParameterNames();
		String param="", param_value="";
		
		String title="";
		
		while(params.hasMoreElements())
		{
			param=params.nextElement();
			param_value=request.getParameter(param);
			
			if(param.equalsIgnoreCase("language"))
			{
				//title=StringUtils.trim(param_value);
				return param_value;
			}
		}
		
		return "";
		
	}
	

	public String parseLocation(HttpServletRequest request)
	{
		Enumeration<String> params=request.getParameterNames();
		String param="", param_value="";
		
		String title="";
		
		while(params.hasMoreElements())
		{
			param=params.nextElement();
			param_value=request.getParameter(param);
			
			if(param.equalsIgnoreCase("location"))
			{
				//title=StringUtils.trim(param_value);
				return param_value;
			}
		}
		
		return "";
		
	}

	public String parseRelation(HttpServletRequest request)
	{
		Enumeration<String> params=request.getParameterNames();
		String param="", param_value="";
		
		String title="";
		
		while(params.hasMoreElements())
		{
			param=params.nextElement();
			param_value=request.getParameter(param);
			
			if(param.equalsIgnoreCase("relation"))
			{
				//title=StringUtils.trim(param_value);
				return param_value;
			}
		}
		
		return "";
		
	}
	public int parsePage(HttpServletRequest request)
	{
		Enumeration<String> params=request.getParameterNames();
		String param="", param_value="";
		
		String title="";
		
		while(params.hasMoreElements())
		{
			param=params.nextElement();
			param_value=request.getParameter(param);
			
			if(param.equalsIgnoreCase("page"))
			{
				//title=StringUtils.trim(param_value);
				return Integer.valueOf(param_value);
			}
		}
		
		return 0;
		
	}

	public String parseKeyword(HttpServletRequest request)
	{
		Enumeration<String> params=request.getParameterNames();
		String param="", param_value="";
		
		String keyword="";
		
		while(params.hasMoreElements())
		{
			param=params.nextElement();
			param_value=request.getParameter(param);
			
			if(param.equalsIgnoreCase("keyword"))
			{
				//title=StringUtils.trim(param_value);
				return param_value;
			}
		}
		
		return "";
		
	}

	public String parseCollectionID(HttpServletRequest request)
	{
		Enumeration<String> params=request.getParameterNames();
		String param="", param_value="";
		
		String keyword="";
		
		while(params.hasMoreElements())
		{
			param=params.nextElement();
			param_value=request.getParameter(param);
			
			if(param.equalsIgnoreCase("id"))
			{
				//title=StringUtils.trim(param_value);
				return param_value;
			}
		}
		
		return "";
		
	}


	public String parseID(HttpServletRequest request)
	{
		Enumeration<String> params=request.getParameterNames();
		String param="", param_value="";
		
		String keyword="";
		
		while(params.hasMoreElements())
		{
			param=params.nextElement();
			param_value=request.getParameter(param);
			
			if(param.equalsIgnoreCase("id"))
			{
				//title=StringUtils.trim(param_value);
				return param_value;
			}
		}
		
		return "";
		
	}


	public String parseISO(HttpServletRequest request)
	{
		Enumeration<String> params=request.getParameterNames();
		String param="", param_value="";
		
		String keyword="";
		
		while(params.hasMoreElements())
		{
			param=params.nextElement();
			param_value=request.getParameter(param);
			
			if(param.equalsIgnoreCase("iso"))
			{
				//title=StringUtils.trim(param_value);
				return param_value;
			}
		}
		
		return "";
		
	}


	public String parseFromDate(HttpServletRequest request)
	{
		Enumeration<String> params=request.getParameterNames();
		String param="", param_value="";
		
		String title="";
		
		while(params.hasMoreElements())
		{
			param=params.nextElement();
			param_value=request.getParameter(param);
			
			if(param.equalsIgnoreCase("from"))
			{
				//title=StringUtils.trim(param_value);
				return param_value;
			}
		}
		
		return "";
		
	}
	
	public String parseURI(HttpServletRequest request)
	{
		Enumeration<String> params=request.getParameterNames();
		String param="", param_value="";
		
		String title="";
		
		while(params.hasMoreElements())
		{
			param=params.nextElement();
			param_value=request.getParameter(param);
			
			if(param.equalsIgnoreCase("uri"))
			{
				//title=StringUtils.trim(param_value);
				return param_value;
			}
		}
		
		return "";
		
	}
	public String parseToDate(HttpServletRequest request)
	{
		Enumeration<String> params=request.getParameterNames();
		String param="", param_value="";
		
		String title="";
		
		while(params.hasMoreElements())
		{
			param=params.nextElement();
			param_value=request.getParameter(param);
			
			if(param.equalsIgnoreCase("to"))
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
