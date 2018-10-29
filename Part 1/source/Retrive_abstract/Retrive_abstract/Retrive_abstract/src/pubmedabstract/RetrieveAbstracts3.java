package pubmedabstract;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
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


public class RetrieveAbstracts3 {


  public static void main(String[] args)
	    {
	        try
	        {
	        	File file=new File("data_breast_cancer/Just_Ids1");
	        	BufferedReader bufferedReader=new BufferedReader(new FileReader(file));
	        	String dd;
	        	String IDs = null;
	        	int i=1,j=1,k=1;
	        	while ((dd=bufferedReader.readLine())!=null)
	        	{
	        		System.out.println(dd);
	        		if(i==1)
	        			IDs=dd;
	        		else {
						IDs+=","+dd;
					}
	        		i++;
	        		k++;
	        		//if(i==200)
	        		//{
	        		URL url = new URL("https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=pubmed&id="+IDs+"&retmode=xml&rettype=medline");
		            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		            conn.setRequestMethod("GET");
		            conn.setRequestProperty("Accept", "application/xml");
		 
		            if (conn.getResponseCode() != 200)
		            {
		                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
		            }
		 
		            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
		           String d;
		           File f = new File("data_breast_cancer/abstracts//"+j+".xml");
		            FileWriter fw= new FileWriter(f.getAbsoluteFile());
		            BufferedWriter bw=new BufferedWriter(fw); 
		           while((d=br.readLine())!=null)
		           {
		           	           // System.out.println(d);
		           	            bw.append(d);
		           }
		          
		           bw.close();
		            conn.disconnect();
		            j++;
		            i=1;
		            IDs=null;
				//}
	        		if(k==10000)
	        			break;
	        	}
	        		
	        	
	        	System.out.println("Done");
//	        	}
	        
	             
	        } catch (MalformedURLException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	
}
