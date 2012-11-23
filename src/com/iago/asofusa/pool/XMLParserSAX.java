package com.iago.asofusa.pool;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;

import com.iago.asofusa.utils.Partido;
import com.iago.asofusa.utils.User;

public class XMLParserSAX {

	private URL rssUrl;

	public XMLParserSAX(String url)
	{
		try
		{
			rssUrl = new URL(url);
		}
		catch (MalformedURLException e)
		{
			throw new RuntimeException(e);
		}
	}

	public List<Partido> parsePartidos()
	{
		SAXParserFactory factory = SAXParserFactory.newInstance();
		
		try
		{
			SAXParser parser = factory.newSAXParser();
			XMLHandlerPartidos handler = new XMLHandlerPartidos();
			
			InputSource is = new InputSource(rssUrl.toString());
			is.setEncoding("ISO-8859-1");
			
			parser.parse(is, handler);
			return handler.getPartidos();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public List<User> parseUsers()
	{
		SAXParserFactory factory = SAXParserFactory.newInstance();
		
		try
		{
			SAXParser parser = factory.newSAXParser();
			XMLHandlerCalificacion handler = new XMLHandlerCalificacion();
			
			InputSource is = new InputSource(rssUrl.toString());
			is.setEncoding("ISO-8859-1");
			
			parser.parse(is, handler);
			return handler.getUsers();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
}
