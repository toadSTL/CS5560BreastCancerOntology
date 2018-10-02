package pubmedabstract;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;


public class MainRetriveFromIDs1 {


  public static void main(String[] args)
	    {
	        try
	        {
//	        	  for(int i=2014;i<=2015;i++)
//	        	{
	           // URL url = new URL("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&term=science%5bjournal%5d+AND+cancer+AND+"+i+"%5bpdat%5d");
	        		  URL url = new URL("https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&term=breast%20cancer%20diagnosis&retmax=40");
	            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	            conn.setRequestMethod("GET");
	            conn.setRequestProperty("Accept", "application/xml");
	 
	            if (conn.getResponseCode() != 200)
	            {
	                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
	            }

	            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
	           String d;
	           File f = new File("data_breast_cancer/ids.xml");
	            FileWriter fw= new FileWriter(f.getAbsoluteFile());
	            BufferedWriter bw=new BufferedWriter(fw); 
	           while((d=br.readLine())!=null)
	           {
	           	            System.out.println(d);
	           	            bw.append(d);
	           }
	           bw.close();
	            conn.disconnect();
//	        	}
	        
	             
	        } catch (MalformedURLException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	
}
