# WCD_probe

A tool to find web cache deception vulnerabilities


## installation

 - `git clone https://github.com/f-min/WCD_probe`
 - `cd WCD_probe-main && mvn clean install`
 
# usage

### before use

You need to edit the /WCD_probe/marker.txt file for the tool to work correctly. It is necessary to enter the markers of the account with which the tests are carried out (i.e. username, email, name, surname, etc.). If the file in question is left blank, the tool may not detect some WCD.
 <br/>An example of such file can be found in the code below:

```
my_username
my_email@email.com
my_address
my_phone_number

```

### usage with the CLI

 - run: `cd target && java -jar WCD-probe.jar --cookies=COOKIES --domain=DOMAIN_TO_ANALYZE`
   where COOKIES, are your session cookies and DOMAIN_TO_ANALYZE is the domain you want to analyze
   
   
 
### usage with the extension

 - Load the browser extension (in this repo under the WCD_probe/extension).
 <br/> For doing this go to: about:debugging , click "This firefox" and then click "Load temporary Add-on" and select the wcd_prober/extension/manifest.json file. <br/>Now you can open the web extension by clicking on his icon (top right) or by visiting moz-extesnion://EXTENSION-ID/WCD.html. <br/>NOTE: To use the extension in Chrome you need to install it with the help of other extensions such as the "firefox Relay" (https://chrome.google.com/webstore/detail/firefox-relay/lknpoadjjkjcmjhbjpcljdednccbldeb).
 
 
 - run `java -jar WCD-probe.jar`
 
 ![image]()
 
 - once authenticated on the domain to test enter the domain name in the extension input and click "start scan". The java program will do the rest of the the job.
 
  ![image]()
  
 
 
If you use the extension you can specify multiple domains separated by carriage returns in the appropriate input textarea, the Java program will queue the requests and execute them one at a time.
 
In every usage case if the tool find a possible WCD, it will append the results in the result.txt file inside the main repo directory and the relative output will be printed on the CLI too.

## configuration file

 - payload list (payload.txt)
   This file contain the web cache deception payload used, feel free to modify the list.<br/>
 - crawling parameters list (crawling.txt)
   This file contain 3 crawling parameters that regulate the recursion of the crawling algorithm.<br/>
   MAX_DEPTH , indicating the max depth of the recursion<br/>
   MAX_AMPLITUDE , indicating the max number of collectable links with the same base path<br/>
   MAX_COLLECTED_PAGES , indicating the max number of link collectable by this phase<br/><br/>
 - marker list (marker.txt)
   This file contain the markers (one per line) to be used to search during the computation.
  
  
## pros and cons

### pros

- tries to cover as many WCD cases as possible by analyzing the content of HTTP responses.
- covers cases with and without HTTP headers related to caching and specific cases of some software
- it has good effectiveness in cases of analysis with authentication

### cons
  

- It requires a certain amount of time
- It is well suited for small scale analysis but not for large scale analysis.
- to correctly detect the WCD it requires user authentication on the domain to be analyzed

 
## limitations and future improvements

The program does not work in cases where the tested application checks the navigator.webdriver JS property (client-side antibot software). The tool proposed here will be updated with new possible attack cases and improvements.




