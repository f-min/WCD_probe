

document.getElementById('button').onclick = function()
{
    
    var list = document.getElementById('list');                             /* retrieves the textarea object from the DOM */
    
    if(list.value != null && !list.value.endsWith("\n"))
    {
        list.value = list.value.concat("\r\n");
    }

    let i = 0;
    let raw_data = "";
    let list_array = list.value.match(/([^\n]+\n)/g);
    
    


    if(list_array == null)
    {
        alert('error: void domain list');

        return 0;
    }
    else
    {
      for (let element of list_array)
      {
        element = element.replace(/\n/g, '');
        
        let gettingAllCookies = browser.cookies.getAll({url: "https://" + element});
        gettingAllCookies.then((cookies) =>
        {
        

            raw_data += 'domain=' + element + '&';
            raw_data += 'cookies=';

            for(let cookie of cookies)
            {
                raw_data += cookie.name + '=' + cookie.value + '; ';
            }
            
            if(cookies == "")
            {
               raw_data += "NOCOOKIES";
            }

            //alert(raw_data);

            

            var xhr = new XMLHttpRequest();
            xhr.open("POST", "http://127.0.0.1:8000/wcd_prober");
            xhr.send(raw_data);
            
            raw_data = "";
        
        });
      }
        
    }
};
