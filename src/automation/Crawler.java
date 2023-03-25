package automation;

import java.net.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.google.common.net.InternetDomainName;


public class Crawler
{
	private int MAX_DEPTH;
    private int MAX_AMPLITUDE;
    private int MAX_COLLECTED_PAGES;
    private HashSet<String> links;
    private String domain;
    private WebDriver driver;
    private ArrayList<String> cookie_names;
    private ArrayList<String> cookie_values;

    //constructor
    
    public Crawler(String domain_input, String cookies_input)
    {
    	ConfHandler conf = new ConfHandler();
		List<Integer> crawlingConf = conf.getCrawlingConf();
    	
		MAX_DEPTH = crawlingConf.get(0);
		MAX_AMPLITUDE = crawlingConf.get(1);
		MAX_COLLECTED_PAGES = crawlingConf.get(2);
		
        links = new HashSet<>();				//instantiate an hashset for saving the collected links
        domain = domain_input;					//save the domain to analyze
       
        System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/chromedriver");		//instantiate the CromeDriver object for headless crawling
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless");
		//options.addArguments("--disable-blink-features=AutomationControlled");
		options.addArguments("user-agent=Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36");
		//options.setPageLoadStrategy(PageLoadStrategy.EAGER);
		driver = new ChromeDriver(options);
		
		driver.get("https://" + domain_input + "/something/inexistent/to/stay_on_domain/and_set_cookies");		//set the session cookies in the browser session
		//System.out.println(cookies_input);
		SetCookieValues(cookies_input, domain_input);
		
    }

    /*************************************************
    * 
    * the recursive crawling procedure
    * 
    **************************************************/
    
    public void getPageLinks(String URL_input, int depth) throws InterruptedException
    {
    	
    	
    	if(URL_input.startsWith("http://" + domain) || URL_input.startsWith("https://" + domain))		//if it's a valid URL
    	{
    		if(URL_input.contains("?"))												//strip the querystring and the fragment part from the URL
	    	{
	    		URL_input = URL_input.substring(0, URL_input.indexOf("?"));
	    	}
	    	if(URL_input.contains("#"))
        	{
        		URL_input = URL_input.substring(0, URL_input.indexOf("#"));
        	}
 
	    	
	    	//System.out.println(URL_input);
	    	
        if(!links.contains(URL_input) && isAmplitudeAllowed(URL_input))		//if the link was not previously saved and the
    	{																	//link base path has not being saved more than the limit
        	links.add(URL_input);											//save the link
        	
    		if(depth < MAX_DEPTH && links.size() < MAX_COLLECTED_PAGES)		//if the recursion depth is lesser then the limit 
    		{																//and the saved links are lesser then the limit
    			System.out.println("reading links at: " + URL_input);
    		

                driver.get(URL_input);		//get the link in the headless browser
                
                Thread.sleep(5000);			//let the page loading
                
                JavascriptExecutor js = (JavascriptExecutor) driver;								//add to every node of the DOM the CSS display:true directive,
                js.executeScript("var all = document.getElementsByTagName(\"*\");\n"				//this will made every element readable by the browser
                		+ "\n"
                		+ "for (var i=0, max=all.length; i < max; i++)\n"
                		+ "{\n"
                		+ "    if(all[i].tagName != \"SCRIPT\" && all[i].tagName != \"STYLE\")\n"
                		+ "    {\n"
                		+ "        all[i].setAttribute('style', 'display: inline');\n"
                		+ "    }\n"
                		+ "}");
                
                Thread.sleep(1000);		//wait a second for the JS to execute
                
                List<WebElement> a_hrefs = driver.findElements(By.tagName("a"));					//get all the anchor elements
				List<WebElement> form_actions = driver.findElements(By.tagName("form"));			//get all the form elements
				
				List<String> href = new ArrayList<>();			//instantiate two lists for saving the link to collect
				List<String> action = new ArrayList<>();
				
				
				//for every anchor element found, if their href value isn't a logout link save the value
				
				for(WebElement _href : a_hrefs)
				{
					if(_href.getAttribute("href") != null && !_href.getAttribute("href").matches("(?i).*logout.*") && !_href.getAttribute("href").matches("(?i).*log_out.*") && !_href.getAttribute("href").matches("(?i).*log-out.*") && !_href.getAttribute("href").matches("(?i).*signout.*") && !_href.getAttribute("href").matches("(?i).*sign-out.*") && !_href.getAttribute("href").matches("(?i).*sign_out.*"))
					{
						href.add(_href.getAttribute("href").toString());
						//System.out.println(_href.getAttribute("href").toString());
					}
                    
                }
				
				//for every form element found, if the action value isn't a logout link save the value
					
				for(WebElement form : form_actions)
				{	
					if(form.getAttribute("action") != null && !form.getAttribute("action").matches("(?i).*logout.*") && !form.getAttribute("action").matches("(?i).*log_out.*") && !form.getAttribute("action").matches("(?i).*log-out.*") && !form.getAttribute("action").matches("(?i).*signout.*") && !form.getAttribute("action").matches("(?i).*sign-out.*") && !form.getAttribute("action").matches("(?i).*sign_out.*"))
					{
						action.add(form.getAttribute("action").toString());
					}
                }
				
                depth++;			//increment the recursive depth level
                
                for(String string : href)			//for every link found in the last 2 for cicles call the recursive procedure
				{
                    getPageLinks(string, depth);
                }
                
                for(String string : action)
				{
                    getPageLinks(string, depth);
                }
        	}   
    		} 
        
    	}
    }
    
    /*******************************************
    * 
    * getter for the saved links hashset
    * 
    ********************************************/
    
    public HashSet<String> getLinks()
    {
    	return links;
    }
    
    /*************************************************************************
    * 
    * procedure that control if a base path has been saved more then the limit
    * 
    **************************************************************************/
    
    
    public boolean isAmplitudeAllowed(String URL_input)
    {
    	int i = 0;
    	String path = "";
    	URL url;
    	
    	try
    	{
    		
    		url = new  URL(URL_input);			//instantiate an URL object
			
    		path = url.getPath();				//get the path of the URL
    		
    		if(path.endsWith("/"))									//if the path end with forward slash remove this forward slash
        	{
        		path = path.substring(0, path.lastIndexOf("/"));
        	}
        	
        	path = path.substring(0, path.lastIndexOf("/") + 1);	//take only in consideration the base path
    		
    		
    		
        	for(String link : links)	//for every saved link
            {
        		try
        		{
        			
	        		if(link.contains("?"))								//strip the link from querystring and fragment
	        		{
	        			link = link.substring(0, link.indexOf("?"));
	        		}
	        		if(link.contains("#"))
	            	{
	            		link = link.substring(0, URL_input.indexOf("#"));
	            	}
	        		
	        		URL _url = new  URL(link);							//instantiate a URL object for that link
	        		
	        		//if the base path is the same of the saved link increment the counter
	        		
	        		if(_url.getPath().contains(path) && getPathLevel(_url.getPath(), 1) == getPathLevel(path, 0))
	            	{
	            		i++;
	            	}
	
	            	if(i > MAX_AMPLITUDE)	//if there are more then MAX_AMPLITUDE equals base path, return false, otherwise true
	            	{
	            		return false;
	            	}
        		}
        		catch(MalformedURLException ex)
            	{
            		System.err.println("For '" + URL_input + "': " + ex.getMessage());
            	}
            }
        	
        	return true;
    	}
    	catch(MalformedURLException ex)
    	{
    		System.err.println("For '" + URL_input + "': " + ex.getMessage());
    	}

    	return false;
    }
    
    /*******************************************************
    * 
    * procedure that return the depth of the input path
    * 
    ********************************************************/
    
    public int getPathLevel(String path, int mode)
    {
    	if(mode == 1)	//if the path in input has not previously being treated
    	{
    		if(path.endsWith("/") && !path.contentEquals("/"))		//if the path end with forward slash, remove the last forward slash
        	{
        		path = path.substring(0, path.lastIndexOf("/"));
        	}
        															//then take in consideration only the base path
        	path = path.substring(0, path.lastIndexOf("/") + 1);
    		
    	}
    	
    	return path.replaceAll("[^/]", "").length();		//return the depth of the base path
    	
    }
    
    /*****************************************************************
    * 
    * Procedure that set the session cookies in the headless browser
    * 
    ******************************************************************/
    
    @SuppressWarnings("deprecation")
	public void SetCookieValues(String cookies_input, String domain_input)
	{
    	driver.manage().deleteAllCookies();				//delete all cookies previously set
    	try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	
		String[] pairs = cookies_input.split("\\; ");	//split the various couples cookiename=cookievalue
    	
		cookie_values = new ArrayList<String>();
		
		cookie_names = new ArrayList<String>();
		
		
		
		for(int i = 0; i < pairs.length; i++)			//for every cookie-name/cookie-value couple
		{ 
			if(!(pairs[i].indexOf("=") == pairs[i].length() - 1))		//if the cookie is not empty save his content and his name
			{
				cookie_values.add(pairs[i].substring(pairs[i].indexOf("=") + 1, pairs[i].length()));
				cookie_names.add(pairs[i].substring(0, pairs[i].indexOf("=")));
				
			}
			
		}
		
		
		
		String[] levels = domain_input.split("\\.");	
		

		if(levels.length > 1)	//if it's not a second level domain extract the second level domain
        {
            domain_input = levels[levels.length - 2] + "." + levels[levels.length - 1];
        }

		
		
		
		for(int i = 0; i < cookie_names.size(); i++)			//for every cookie set the cookie in the headless browser
		{
			
			Cookie cookie = new Cookie.Builder(cookie_names.get(i), cookie_values.get(i))
				    .domain(domain_input)
				    .expiresOn(new Date(125, 10, 11))
				    .isHttpOnly(false)
				    .isSecure(true)
				    .path("/")
				    .build();
				driver.manage().addCookie(cookie);
				//System.out.println(cookie_names.get(i) + " = " + cookie_values.get(i));
		}
	}
    
    /********************************************************************
    * 
    * procedure to check whether a domain is a subdomain of another one
    * 
    *********************************************************************/
    
    public static boolean isSubDomain(String str, String domain)
    {
    	if(InternetDomainName.isValid(domain) && InternetDomainName.isValid(str))
    	{
	    	domain = InternetDomainName.from(domain).topDomainUnderRegistrySuffix().toString();
	    	
	        str = InternetDomainName.from(str).topDomainUnderRegistrySuffix().toString();
	        
	        if(domain.equals(str))
	        {
	        	return true;
	        }
    	}
        return false;
    }
    
    //procedure to close the headless browser when required
    
    public void closeDriver()
    {
    	driver.close();
    }
    
    /*
    public static void main(String[] args)
    {
    	String userDirectory = System.getProperty("user.dir");
    	System.out.println(userDirectory);
    }
    */
    
    
    
    
    
}