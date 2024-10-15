package automation;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConfHandler
{
	
	public List<Integer> getCrawlingConf()
	{
		List<String> lines = new ArrayList<String>();
		List<Integer> crawlingConf = new ArrayList<>();
		
		try
		{
			  String userDirectory = System.getProperty("user.dir");
	    	  //System.out.println(userDirectory);
		      File myObj = new File(userDirectory + "/../crawling.txt");
		      Scanner myReader = new Scanner(myObj);
		      
		      while(myReader.hasNextLine())
		      {
		        lines.add(myReader.nextLine());
		        //System.out.println(lines.get(i));
		      }
		      
		        for(int j = 0; j < lines.size(); j++)
		        {
		           switch(j)
		           {
		        	   case 0:
		        	   {
		        		   String[] fields = lines.get(j).split("MAX_DEPTH=");
		        		   crawlingConf.add(Integer.parseInt(fields[1]));
		        		   //System.out.println(crawlingConf[j]);
		        		   break;
		        	   }
		        	   case 1:
		        	   {
		        		   String[] fields = lines.get(j).split("MAX_AMPLITUDE=");
		        		   crawlingConf.add(Integer.parseInt(fields[1]));
		        		   //System.out.println(crawlingConf[j]);
		        		   break;
		        	   }
		        	   case 2:
		        	   {
		        		   String[] fields = lines.get(j).split("MAX_COLLECTED_PAGES=");
		        		   crawlingConf.add(Integer.parseInt(fields[1]));
		        		   //System.out.println(crawlingConf[j]);
		        		   break;
		        	   }
		           }
		           
		        }
		      
		        /*for(int i = 0; i < crawlingConf.size(); i++)
		        {
		        	System.out.println(crawlingConf.get(i));
		        }*/
		        
		      myReader.close();
		      return crawlingConf;
		    }
			catch(FileNotFoundException e)
			{
		      System.out.println("An error occurred.");
		      e.printStackTrace();
			}
		return crawlingConf;
	}
	
	public List<String> getPayloadList()
	{
		List<String> lines = new ArrayList<String>();
		
		try
		{
			  String userDirectory = System.getProperty("user.dir");
	    	  //System.out.println(userDirectory);
		      File myObj = new File(userDirectory + "/../payload.txt");
		      Scanner myReader = new Scanner(myObj);
		      
		      
		      
		      while(myReader.hasNextLine())
		      {
		        lines.add(myReader.nextLine());
		        //System.out.println(lines.get(i));
		      }
		      
		       /* for(int j = 0; j < lines.size(); j++)
		        {
		           System.out.println(lines.get(j));
		        }*/

		      
		      myReader.close();
		      return lines;
		    }
			catch(FileNotFoundException e)
			{
		      System.out.println("An error occurred.");
		      e.printStackTrace();
			}
		return lines;
	}
	
	public List<String> getMarkerList()
	{
		List<String> lines = new ArrayList<String>();
		
		try
		{
			  String userDirectory = System.getProperty("user.dir");
	    	  //System.out.println(userDirectory);
		      File myObj = new File(userDirectory + "/../marker.txt");
		      Scanner myReader = new Scanner(myObj);
		      
		      
		      
		      while(myReader.hasNextLine())
		      {
		        lines.add(myReader.nextLine());
		        //System.out.println(lines.get(i));
		      }
		      
		       /* for(int j = 0; j < lines.size(); j++)
		        {
		           System.out.println(lines.get(j));
		        }*/

		      
		      myReader.close();
		      return lines;
		    }
			catch(FileNotFoundException e)
			{
		      System.out.println("An error occurred.");
		      e.printStackTrace();
			}
		return lines;
	}
	
}
