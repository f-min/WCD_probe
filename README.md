# WCD prober

A tool to find web cache deception vulnerabilities


## prerequisites

 - download this repo and unzip it.
 - install Chrome on your machine.
 - download the chromedriver here: https://chromedriver.chromium.org/downloads, download the same version of your Chrome installation. Then move it to the program directory (the main repository directory).
 

# usage

### before use

You need to edit the /WCD_prober/conf/marker.txt file for the tool to work correctly. It is necessary to enter the markers of the account with which the tests are carried out (i.e. username, email, name, surname, etc.). If the file in question is left blank, the tool may not detect some WCDs.
 <br/>An example of such file can be found in the code below:

```
my_username
my_email@email.com
my_address
my_phone_number
```

### usage from the CLI

 - run: `java -jar WCD_prober.jar --cookies=COOKIES --domain=DOMAIN_TO_ANALYZE`
   where COOKIES, are your session cookies and DOMAIN_TO_ANALYZE is the domain you want to analyze
   
   
 
### usage with the extension

 - Load the browser extension (in this repo under the wcd_prober/extension).
 <br/> For doing this go to: about:debugging , click "This firefox" and then click "Load temporary Add-on" and select the wcd_prober/extension/manifest.json file. <br/>Now you can open the web extension by clicking on his icon (top right) or by visiting moz-extesnion://EXTENSION-ID/WCD.html. <br/>NOTE: To use the extension in Chrome you need to install it with the help of other extensions such as the "firefox Relay" (https://chrome.google.com/webstore/detail/firefox-relay/lknpoadjjkjcmjhbjpcljdednccbldeb).
 
 
 - run `java -jar WCD_prober.jar`
 
 ![image](https://github.com/f-min/WCD_prober/blob/mainx/img/cli.png)
 
 - once authenticated on the domain to test enter the domain name in the extension input and click "start scan". The java program will do the rest of the the job.
 
  ![image](https://github.com/f-min/WCD_prober/blob/mainx/img/screen.png)
  
 
 
NOTE: it is advisable to use WCD_prober with the extension because you don't need to copy and paste the domain cookies in the CLI. By using the extension you can also insert more then one domain to analyze and the Java program will queue these computation.
 
 
In every usage case if the tool find a possible WCD, it will append the results in the result.txt file inside the main repo directory and the relative output will be printed on the CLI too.

## configuration file

 - payload list (payload.txt)
   This file contain the web cache deception payload used, feel free to modify the list.<br/><br/>
 - crawling parameters list (crawling.txt)
   This file contain 3 crawling parameters that regulate the recursion of the crawling algorithm.<br/>
   MAX_DEPTH , indicating the max depth of the recursion<br/>
   MAX_AMPLITUDE , indicating the max number of collectable links with the same base path<br/>
   MAX_COLLECTED_PAGES , indicating the max number of link collectable by this phase<br/><br/>
 - marker list (marker.txt)
   This file contain the markers (one per line) to be used to search during the computation.
  
 
## limitation and future improvement

It doesn't work in presence of antibot software that check the navigator.webdriver Javascript property. For bypassing this software protection you can use a modified browser dirver, in the future i'm planning for integrate that here.
This tool was build to cover the majority of cases of Web cache deception vulnerbilies, independently from the underlaying web app infrastructure.
It require authentication to work, so you have to be authenticated via your browser and then run WCD_prober, via web-extension or via CLI.


