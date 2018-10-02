package pubmedabstract;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLReaderIDRetrival {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
//			for(int i=2014;i<2015;i++)
//			{
			
			
			 File file = new File("new_data_alzimer's//ids.xml");
		         if(file.exists())
			  {
			  DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			  DocumentBuilder db = dbf.newDocumentBuilder();
			  Document doc = db.parse(file);
			  doc.getDocumentElement().normalize();
			  System.out.println("Root element " + doc.getDocumentElement().getNodeName());
			  NodeList nodeLst = doc.getElementsByTagName("IdList");
			  System.out.println("Information of all employees");
			  PrintStream outPrintStream=new PrintStream(new File("new_data_alzimer's//Just_Ids"));
		      
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
			
		} catch (DOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
