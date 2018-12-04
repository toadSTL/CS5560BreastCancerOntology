import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class AbstractRetriever {
    static String searchURL = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&term=";
    static String searchTerms = "(breast%20cancer)%20AND%20diagnosis";
    static String searchOptions = "&retmax=750";


    public static void main(String[] args){
        GetIDsXML("meta/ids.xml");
        IDsFromXML("meta/ids.xml", "meta/ids.txt");
        GetAbsXML("meta/ids.txt","abs/xml/");
    }


    public static void AbsFromXML(){

    }


    public static void GetAbsXML(String inPath, String outPath){
        try {
            File file=new File(inPath);
            BufferedReader bufferedReader=new BufferedReader(new FileReader(file));
            String dd;
            String IDs = null;
            int i=1,j=1,k=1;
            while ((dd=bufferedReader.readLine())!=null) {
//                System.out.println(dd);
                if(i==1)
                    IDs=dd;
                else {
                    IDs+=","+dd;
                }
                i++;
                k++;

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
                File f = new File(outPath+j+".xml");
                FileWriter fw= new FileWriter(f.getAbsoluteFile());
                BufferedWriter bw=new BufferedWriter(fw);
                while((d=br.readLine())!=null)
                {
//                    System.out.println(d);
                    bw.append(d);
                }

                bw.close();
                conn.disconnect();
                j++;
                i=1;
                IDs=null;

                if(k==10000)
                    break;
            }

            System.out.println("Done");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void IDsFromXML(String inPath, String outPath){
        try{
            File file = new File(inPath);
            if(file.exists()) {
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    Document doc = db.parse(file);
                    doc.getDocumentElement().normalize();
                    System.out.println("Root element " + doc.getDocumentElement().getNodeName());
                    NodeList nodeLst = doc.getElementsByTagName("IdList");
//                    System.out.println("Information of all employees");
                    PrintStream outPrintStream=new PrintStream(new File(outPath));

                    for (int s = 0; s < nodeLst.getLength(); s++) {

                        Node fstNode = nodeLst.item(s);
                        System.out.println(fstNode.getNodeValue());
                        if (fstNode.getNodeType() == Node.ELEMENT_NODE) {

                            Element fstElmnt = (Element) fstNode;
                            System.out.println(fstElmnt.getNodeValue());
                            NodeList fstNmElmntLst = fstElmnt.getElementsByTagName("Id");
                            String IDs = null;
                            for(int t=0;t<fstNmElmntLst.getLength();t++)
                            {
                                Element fstNmElmnt = (Element) fstNmElmntLst.item(t);
                                NodeList fstNm = fstNmElmnt.getChildNodes();
                                IDs+=((Node) fstNm.item(0)).getNodeValue()+",";
                                //  System.out.println("ID : "  + ((Node) fstNm.item(0)).getNodeValue());
                                outPrintStream.println(((Node) fstNm.item(0)).getNodeValue().toString());
                            }
                            System.out.println("Done");
                        }
                    }
                    outPrintStream.close();
                }
//			}

            } catch (DOMException |
                ParserConfigurationException |
                SAXException |
                IOException e) {
                e.printStackTrace();
            }
    }

    public static void GetIDsXML(String path){
        try
        {
            URL url = new URL(searchURL+searchTerms+searchOptions);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/xml");

            if (conn.getResponseCode() != 200)
            {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String d;
            File f = new File(path);
            FileWriter fw= new FileWriter(f.getAbsoluteFile());
            BufferedWriter bw=new BufferedWriter(fw);
            while((d=br.readLine())!=null)
            {
                System.out.println(d);
                bw.append(d);
            }
            bw.close();
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
