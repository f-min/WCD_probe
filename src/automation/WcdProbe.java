package automation;

import java.io.*;
import java.util.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;

 
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

import com.google.common.net.InternetDomainName;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/*****************************************************************************************************************************
* 
* This tool automate the process
* of searching for web cache deception vulnerabilities.
* 
******************************************************************************************************************************/

//MAIN

public class WcdProbe
{

   public static void main(String[] args) throws Exception
   {
	   
	   if(args.length == 0)
	   {
		   HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);		//instantiate an HTTP server listening on port 8000
	       server.createContext("/wcd_probe", new MyHandler());						//set the "/wcd_probe" endpoint handler
	       server.setExecutor(null);
	       server.start();
	       System.out.println("\n\n  __     __     ______     _____        ______   ______     ______     ______     ______         \n"
	       		+ " /\\ \\  _ \\ \\   /\\  ___\\   /\\  __-.     /\\  == \\ /\\  == \\   /\\  __ \\   /\\  == \\   /\\  ___\\      \n"
	       		+ " \\ \\ \\/ \".\\ \\  \\ \\ \\____  \\ \\ \\/\\ \\    \\ \\  _-/ \\ \\  __<   \\ \\ \\/\\ \\  \\ \\  __<   \\ \\  __\\      \n"
	       		+ "  \\ \\__/\".~\\_\\  \\ \\_____\\  \\ \\____-     \\ \\_\\    \\ \\_\\ \\_\\  \\ \\_____\\  \\ \\_____\\  \\ \\_____\\  \n"
	       		+ "   \\/_/   \\/_/   \\/_____/   \\/____/      \\/_/     \\/_/ /_/   \\/_____/   \\/_____/   \\/_____/     \n"
	       		+ " \n\n                                                          Written by Francesco Minetti\n\n\n");
	   
	   }
	   else				//handle the CLI arguments
	   {
		   if(args.length != 2)
		   {
			   System.out.println("Error: the program need 2 arguments from the CLI interface (--cookies=COOKIES and --domain=DOMAIN)");
		   }
		   else
		   {
			   
			   if(args[0].startsWith("--cookies="))
			   {
				   if(args[1].startsWith("--domain="))
				   {
					   if(InternetDomainName.isValid(args[1].substring(args[1].indexOf("=") + 1, args[1].length())))
					   {
						   WcdProbe probe = new WcdProbe();
						   probe.printTitle();
				           probe.scan(args[1].substring(args[1].indexOf("=") + 1, args[1].length()), args[0].substring(args[0].indexOf("=") + 1, args[0].length()));
					   }
					   else
					   {
						   System.out.println("Error: you entered an incorrect domain name...");
					   }
				   }
				   else
				   {
					  System.out.println("Error: the program need 2 arguments from the CLI interface (--cookies=COOKIES and --domain=DOMAIN)");
				   }
			   }
			   else
			   {
				   if(args[0].startsWith("--domain="))
				   {
					   if(args[1].startsWith("--cookies="))
					   {
						   if(InternetDomainName.isValid(args[0].substring(args[0].indexOf("=") + 1, args[0].length())))
						   {
							   WcdProbe probe = new WcdProbe();
							   probe.printTitle();
					           probe.scan(args[0].substring(args[0].indexOf("=") + 1, args[0].length()), args[1].substring(args[1].indexOf("=") + 1, args[1].length()));
						   }
						   else
						   {
							   System.out.println("Error: you entered an incorrect domain name...");
						   }
					   }
					   else
					   {
						  System.out.println("Error: the program need 2 arguments from the CLI interface (--cookies=COOKIES and --domain=DOMAIN)");
					   }
				   }
				   else
				   {
					   System.out.println("Error: the program need 2 arguments from the CLI interface (--cookies=COOKIES and --domain=DOMAIN)");
				   }
			   }
		   }
	   }
   }

   static class MyHandler implements HttpHandler
   {
       @Override
       public void handle(HttpExchange t) throws IOException
       {
	       	Headers headers = t.getResponseHeaders();						//get the standard response header
	       	InputStream in = t.getRequestBody();							//get the request body, interpreted as UTF-8 chars
	       	String result = IOUtils.toString(in, StandardCharsets.UTF_8);
	       	
	       	String[] pairs = result.split("\\&", 2);						//split the body in two parts
       	
           
           String[] fields = pairs[0].split("domain=");						//save the first parameter value(the domain to analyze)
           String domain = URLDecoder.decode(fields[1], "UTF-8");
           
           if(InternetDomainName.isValid(domain))
           {
        	   String[] fields_1 = pairs[1].split("cookies=");					//save the second parameter value(cookies)
               String cookies = fields_1[1];
               
               if(cookies == "NOCOOKIES")										
               {
               	cookies = "";//System.out.println("no cookies for the domain...");
               }
               
               System.out.println("running scan on " + domain + "\r\n");
           	
           	   headers.add("Access-control-allow-origin", "*");					//return a 200 response to the client
               t.sendResponseHeaders(200, 0);
               
               OutputStream os = t.getResponseBody();							//close the incoming stream of bytes for the request
               os.close();
               
               WcdProbe probe = new WcdProbe();
               probe.scan(domain, cookies);
           }
           else
           {
        	   System.out.println("Error: you entered an incorrect domain name...");
           }
           
       }
	}
   
   public void scan(String domain, String cookies)
   {
	   HashSet<String> links = new HashSet<String>();					//create an hashset of String
       
       System.out.println("collecting pages on " + domain + "\r\n");
       
       Crawler crawler = new Crawler(domain, cookies);					//instantiate the Crawler object
       
       try
       {
    	   crawler.getPageLinks("https://" + domain + "/", 0);			//start the crawling
       }
       catch(InterruptedException e)
       {
    	   e.printStackTrace();
       }
       
       links = crawler.getLinks();				//save the link collected by the Crawler object
       
       crawler.closeDriver();					//close the headless browser via selenium webdriver(chromedriver)
       
       /*for(String link : links)					
       {
       		//System.out.println(link);
       }*/
       
       System.out.println("detecting wcd on " + domain + "\r\n");
       
       Detector detector = new Detector(cookies, links, domain);		//start the detection phase
       detector.detect();
       
       System.out.println("\r\nscan finished...\r\n\r\n");
   }
   
   public void printTitle()
   {
	   System.out.println("\n\n  __     __     ______     _____        ______   ______     ______     ______     ______         \n"
	       		+ " /\\ \\  _ \\ \\   /\\  ___\\   /\\  __-.     /\\  == \\ /\\  == \\   /\\  __ \\   /\\  == \\   /\\  ___\\      \n"
	       		+ " \\ \\ \\/ \".\\ \\  \\ \\ \\____  \\ \\ \\/\\ \\    \\ \\  _-/ \\ \\  __<   \\ \\ \\/\\ \\  \\ \\  __<   \\ \\  __\\      \n"
	       		+ "  \\ \\__/\".~\\_\\  \\ \\_____\\  \\ \\____-     \\ \\_\\    \\ \\_\\ \\_\\  \\ \\_____\\  \\ \\_____\\  \\ \\_____\\  \n"
	       		+ "   \\/_/   \\/_/   \\/_____/   \\/____/      \\/_/     \\/_/ /_/   \\/_____/   \\/_____/   \\/_____/     \n"
	       		+ " \n\n                                                          Written by Francesco Minetti\n\n\n");
	   
   }

}
