package automation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class DetectorThread extends Thread
{
	private String[] marker;					//set in registration phase as username, firstname and lastname
					             				//phone number
					              				//email address
					             				//physical address
					            								

	//web cache deception payload
	
	private String[] suffix;
	           		
	private String cookies;
	private ArrayList<String> cookie_values;
	private String host;
	private boolean isFirstDirLevel;
	private boolean isMagentoCache = false;
	private String magentoVaryCookieValue;
	private String link;
	private Detector detector;
	private Pattern p;

	
	//constructor
	
    public DetectorThread(Detector detector, String link_input)
    {
    	marker = new String[detector.marker.size()];
    	suffix = new String[detector.suffix.size()];
    	
    	for(int i = 0; i < detector.marker.size(); i++)
    	{
    		marker[i] = detector.marker.get(i);
    		//System.out.println(marker[i]);
    	}
    	
    	for(int i = 0; i < detector.suffix.size(); i++)
    	{
    		suffix[i] = detector.suffix.get(i);
    		//System.out.println(suffix[i]);
    	}
    	
    	p = detector.p;
    	cookies = detector.getCookies();
    	cookie_values = detector.getCookie_values();
    	host = detector.getHost();
    	isMagentoCache = detector.getIsMagentoCache();
    	magentoVaryCookieValue = detector.getMagentoVaryValue();
        link = link_input;
        detector.incrementActiveThread();
        this.detector = detector;
    }

    //thread main procedure
    
    public void run()
    {
    	
    	if(link.chars().filter(num -> num == '/').count() == 3 && link.endsWith("/")) //if it's the homepage link
		{
			isFirstDirLevel = true;
			
			for(int i = 0; i < suffix.length; i++)
			{
				if(suffix[i].startsWith("/"))
				{
					suffix[i] = suffix[i].substring(1, suffix[i].length());
				}
			}
		}
		else
		{
			isFirstDirLevel = false;
			
			if(link.endsWith("/") && link.chars().filter(num -> num == '/').count() > 3)	//if the link end with forward slash
        	{
        		link = link.substring(0, link.lastIndexOf("/"));		//remove the last forward slash
        	}
		}
    	
    	System.out.println("trying wcd with link:  " + link);
    	
    	outerloop:
        for(int i = 0; i < suffix.length; i++)		//for every WCD payload
		{
			if(isFirstDirLevel && (suffix[i].startsWith(".") || suffix[i].equals("")))
			{
				continue;
			}
			
			Document unauth_response;
			Document auth_response;
			
			try
			{
					// send an authenticated request to emulate the victim clicking a malicious link
				
				 auth_response = Jsoup.connect(link + suffix[i] + "?wcdtest=123")
		         		.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
		         	    .header("Accept-Encoding", "gzip, deflate")
		         	    .header("Accept-Language", "it-IT,it;q=0.8,en-US;q=0.5,en;q=0.3")
		         	    .header("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:105.0) Gecko/20100101 Firefox/105.0")
		         	    .header("Host", host)
		         	    .header("Cookie", cookies)
		         	    .ignoreContentType(true)
		                .followRedirects(false)
		         	    .ignoreHttpErrors(true)
		         	    .get();
				 
				 try
				 {
					 Thread.sleep(1500);
				 }
				 catch(InterruptedException e)
				 {
					 System.out.println("detector error...");
				 }
				 
				 if(isMagentoCache)	//if the application use Magento openmage LTS send a request to emulate the attacker request with the proper openmage LTS cookie
				 {
					 unauth_response = Jsoup.connect(link + suffix[i] + "?wcdtest=123")
			             		.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
			             	    .header("Accept-Encoding", "gzip, deflate")
			             	    .header("Accept-Language", "it-IT,it;q=0.8,en-US;q=0.5,en;q=0.3")
			             	    .header("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:105.0) Gecko/20100101 Firefox/105.0")
			             	    .header("Host", host)
			             	    .header("Cookie", "X-Magento-Vary=" + magentoVaryCookieValue)
			             	    .ignoreContentType(true)
			                    .followRedirects(false)
			             	    .ignoreHttpErrors(true)
			             	    .get();
				 }
				 else		//otherwise send the classic attacker request
				 {
					 unauth_response = Jsoup.connect(link + suffix[i] + "?wcdtest=123")
		             		.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
		             	    .header("Accept-Encoding", "gzip, deflate")
		             	    .header("Accept-Language", "it-IT,it;q=0.8,en-US;q=0.5,en;q=0.3")
		             	    .header("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:105.0) Gecko/20100101 Firefox/105.0")
		             	    .header("Host", host)
		             	    .ignoreContentType(true)
		                    .followRedirects(false)
		             	    .ignoreHttpErrors(true)
		             	    .get();
				 }
				 
				 
				 if(isAuthContent(unauth_response, auth_response))	//if the unauthenticated request contain some victim content write it in the output
				 {
					 try(FileWriter fw = new FileWriter(System.getProperty("user.dir") + "/results.txt", true);
							    BufferedWriter bw = new BufferedWriter(fw);
							    PrintWriter out = new PrintWriter(bw))
							{
							    out.println("Web Cache Deception found at: " + link + suffix[i]);
							    
							}
							catch(IOException e) 
							{
							    System.out.println(e.getMessage().toString());
							}
					 System.out.println("Web Cache Deception found at: " + link + suffix[i]);
					 break outerloop;
				 }
				 
			}
			catch(IOException e)
		    {
		        System.err.println("For '" + link + suffix[i] + "': " + e.getMessage());
		    }
			
		}

    	//reset the payload list if the input link was the homepage link
    	
		if(isFirstDirLevel)
		{
			for(int i = 8; i < 15; i++)
			{
				suffix[i] = "/" + suffix[i];
			}
		}
		
		detector.decrementActiveThread();
    }

    /***********************************************************************************************
	* 
	* procedure that check if the application returned some victim content in the attacker request
	* 
	************************************************************************************************/
    
    public boolean isAuthContent(Document unauth_response, Document auth_response)
	{
		String response = unauth_response.toString();
		
		 //System.out.println("evaluating markers...");
		
		for(int i = 0; i < marker.length; i++)	//for every marker set in the registration phase
		{
			if(isMarkerOrCookieCached(marker[i], response))	//if this marker appear in the attacker's response
			{
				try(FileWriter fw = new FileWriter(System.getProperty("user.dir") + "/results.txt", true);	//wirte in a file and in the stdout the finding and stop the execution
					    BufferedWriter bw = new BufferedWriter(fw);
					    PrintWriter out = new PrintWriter(bw))
					{
					    out.println("marker found: " + marker[i]);
					    
					}
					catch(IOException e) 
					{
					    System.out.println(e.getMessage().toString());
					}

				System.out.println("marker found: " + marker[i]);
				return true;
			}
		}
		
		
		for(int i = 0; i < cookie_values.size(); i++)	//for every victim's cookie
		{
			//if the cookie is lesser then 16 chars long or it's a variation of the hostname skip the computation
			
			if(cookie_values.get(i).length() < 16 || cookie_values.get(i).startsWith("https://" + this.host) || cookie_values.get(i).startsWith("http://" + this.host) || cookie_values.get(i).startsWith(this.host))
			{
				continue;
			}
			
			
			if(isMarkerOrCookieCached(cookie_values.get(i), response))	//if this cookie appear in the attacker's response
			{
				try(FileWriter fw = new FileWriter(System.getProperty("user.dir") + "/results.txt", true);	//wirte in a file and in the stdout the finding and stop the execution
					    BufferedWriter bw = new BufferedWriter(fw);
					    PrintWriter out = new PrintWriter(bw))
					{
					    out.println("cookie found: " + cookie_values.get(i));
					    
					}
					catch(IOException e) 
					{
					    System.out.println(e.getMessage().toString());
					}
				
				System.out.println("cookie found: " + cookie_values.get(i));
				return true;
			}
		}
		
		if(isCsrfTokenCached(auth_response, unauth_response))	//if there is a victim's csrf token in the attacker's response
		{
			try(FileWriter fw = new FileWriter(System.getProperty("user.dir") + "/results.txt", true);	//wirte the finding in the results file and in stdout
				    BufferedWriter bw = new BufferedWriter(fw);
				    PrintWriter out = new PrintWriter(bw))
				{
				    out.println("CSRF token found in cached resource");
				    
				}
				catch(IOException e) 
				{
				    System.out.println(e.getMessage().toString());
				}
			System.out.println("CSRF token found in cached resource");
			return true;
		}
		
			
		return false;	//return false if no web cache deception was deteced
		
	}
	
    /*************************************************************
	* 
	* procedure that check if a string appear in the response 
	* 
	**************************************************************/
    
	public boolean isMarkerOrCookieCached(String marker, String response)
	{
		
		if(response.contains(marker))		//if one string appear in the other one return true, otherwise false
		{
			return true;
		}
		
		return false;
		
	}
	
	
	/***************************************************************************************
	* 
	* procedure for checking if there is a victim's csrf token in the attacker's response
	* 
	****************************************************************************************/
	
	public boolean isCsrfTokenCached(Document auth_response, Document unauth_response)
	{
		String _auth_response = auth_response.toString();			//convert the Jsoup Document object to string
		String _unauth_response = unauth_response.toString();
		
		
		//try to find a match with the authenticated response
		
		Matcher m = p.matcher(_auth_response);
        
        if(m.find())									//if in the victim's response there is a CSRF token
        {
            String match = m.group();
            
            if(_unauth_response.contains(match))		//and this CSRF token is either in the attacker response return true
            {
            	return true;
            }
        }
        
        return false;	//otherwise return false
	}
	
}
