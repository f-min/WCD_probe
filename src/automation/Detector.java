package automation;


import java.util.ArrayList;


import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;


public class Detector
{
	public Pattern p;
	public List<String> marker;
	public List<String> suffix;
	private String cookies;						//session cookies string
	private ArrayList<String> cookie_values;	//session cookie values list
	private HashSet<String> links;				//link collected in the crawling phase
	private String host;						//domain to analyze
	private boolean isMagentoCache = false;		//flag to indicate the presence of Magento Openmage LTS
	private String magentoVaryCookieValue;		//X-Magento-Vary cookie value
	private int cuncurrent_thread = 0; 			//thread counter
	
	//various getter and setter

	public void incrementActiveThread()
	{
		cuncurrent_thread = cuncurrent_thread++;
	}
	
	public void decrementActiveThread()
	{
		cuncurrent_thread = cuncurrent_thread--;
	}
	
	public String getCookies()
	{
		return cookies;
	}
	
	public ArrayList<String> getCookie_values()
	{
		return cookie_values;
	}
	
	
	public ArrayList<String> getPayloads()
	{
		return cookie_values;
	}
	
	public HashSet<String> getLinks()
	{
		return links;
	}
	
	public String getHost()
	{
		return host;
	}
	
	public boolean getIsMagentoCache()
	{
		return isMagentoCache;
	}
	
	public String getMagentoVaryValue()
	{
		return magentoVaryCookieValue;
	}
	
	//constructor
	
	public Detector(String cookies_input, HashSet<String> links_input, String domain)
	{
		p = Pattern.compile(".*[=|:](?)['|\"][a-z|A-Z|0-9|=|-|+|/|_]{20,100}(?)['|\"].*");
	    
		ConfHandler conf = new ConfHandler();
		marker = conf.getMarkerList();
		suffix = conf.getPayloadList();
		
		cookies = cookies_input;						//save the input cookies

		host = domain;									//save the input host (the domain to analyze)
		
		String[] pairs = cookies_input.split("\\; ");	//split every couple cookie-name/cookie-value
    	
		cookie_values = new ArrayList<String>();		//instantiate the list of cookie values
		
		for(int i = 0; i < pairs.length; i++)	//for every cookie
		{
			if(pairs[i].substring(0, pairs[i].indexOf("=")).toLowerCase().contains("x-magento-vary"))		//if there is a Magento openmage LTS cookie set the proper flag
			{
				isMagentoCache = true;
				magentoVaryCookieValue = pairs[i].substring(pairs[i].indexOf("=") + 1, pairs[i].length());
			}
		}
		
		for(int i = 0; i < pairs.length; i++)	//for every cookie if it's not empty, save his value
		{
			if(!(pairs[i].indexOf("=") == pairs[i].length() - 1))
			{
				cookie_values.add(pairs[i].substring(pairs[i].indexOf("=") + 1, pairs[i].length()));
			}
			
		}
		 
		links = links_input;
		
	}
	
	/***********************************************************
	* 
	* procedure to start the detecion phase
	* 
	************************************************************/
	
	public void detect()
	{
		for(String link : links)			//for every link saved during crawling
		{
			while(countThreadsOfClass(DetectorThread.class) > 3)	//while there are more then 3 thread in execution
			{
				//System.out.println(countThreadsOfClass(DetectorThread.class));
			}
			
			Thread thread = new DetectorThread(this, link);		//start a new detection thread
			thread.start();
		}
		
		while(countThreadsOfClass(DetectorThread.class) > 0)		//while there are thread in execution, let they finish
		{
			//System.out.println(countThreadsOfClass(DetectorThread.class));
		}
		
	}
	
	/******************************************************
	* 
	* procedure to count the thread of one specific class
	* 
	*******************************************************/
	
	
	public int countThreadsOfClass(Class<? extends Thread> clazz)
	{
	    Thread[] tarray = new Thread[Thread.activeCount()];
	    Thread.enumerate(tarray);
	    int count = 0;
	    
	    for(Thread t : tarray)
	    {
	        if(clazz.isInstance(t))
	        {
	            count++;
	        }
	    }
	    return count;
	}
}
