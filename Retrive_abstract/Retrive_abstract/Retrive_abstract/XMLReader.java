package retrival;

import java.io.File;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLReader {

	public static HashMap<String, String> Retrive_Abstract(int i) {
		
		HashMap<String, String> hm= new HashMap<>();

		try {
			//for (int i = 1900; i <= 2015; i++) {
				i=2015;
				File file = new File("abstracts\\abstract" + i + ".xml");
				if(file.exists())
				{
				DocumentBuilderFactory dbf = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document doc = db.parse(file);
				doc.getDocumentElement().normalize();
				//System.out.println("Root element "+ doc.getDocumentElement().getNodeName());
				NodeList nodeLst = doc.getElementsByTagName("PubmedArticle");
				//System.out.println("Information of all Article");

				for (int s = 0; s < nodeLst.getLength(); s++) {

					Node Node1 = nodeLst.item(s);
					if (Node1.getNodeType() == Node.ELEMENT_NODE) {

						Element Element1 = (Element) Node1;
						NodeList Nodelist1 = Element1.getElementsByTagName("MedlineCitation");
						Node Node2 = Nodelist1.item(0);
						if (Node2.getNodeType() == Node.ELEMENT_NODE) {

						
						Element Element2=(Element) Node2;
						NodeList ArticleTitle = Element2.getElementsByTagName("ArticleTitle");
						String at=((Node) ArticleTitle.item(0)).getTextContent();
						at=at.replace("\n"," ");
						at=at.trim().replaceAll("(\t)+", " ");
						at=at.trim().replaceAll("(	)+", " ");
						//System.out.println("Article Title: " + at );
						//hm.put(at, null);
						NodeList NodeList2=Element2.getElementsByTagName("Abstract");
						Node Node3=NodeList2.item(0);
						if (Node3==null) {
							//System.out.println("No Abstract found!!!");
							hm.put(at, null);
						}else if(Node3.getNodeType()==Node.ELEMENT_NODE)
						{
						
							Element FinalElement = (Element) NodeList2.item(0);
							NodeList AbstractText = FinalElement.getElementsByTagName("AbstractText");
							String abt=((Node) AbstractText.item(0)).getTextContent();
							abt=abt.replace("\n","");
							abt=abt.trim().replaceAll("(\t)+", " ");
							abt=abt.trim().replaceAll("(	)+", " ");
							//abt=abt.trim().replaceAll("( )+", " ");
							//System.out.println("Abstract: " + abt );
							hm.put(at, abt);
						}
						}
						/*String d;
						File f = new File("abstract" + i + ".xml");
						FileWriter fw = new FileWriter(f.getAbsoluteFile());
						BufferedWriter bw = new BufferedWriter(fw);

						bw.close();
*/
					}

					/*
					 * NodeList lstNmElmntLst =
					 * fstElmnt.getElementsByTagName("lastname"); Element
					 * lstNmElmnt = (Element) lstNmElmntLst.item(0); NodeList
					 * lstNm = lstNmElmnt.getChildNodes();
					 * System.out.println("Last Name : " + ((Node)
					 * lstNm.item(0)).getNodeValue());
					 */
				}
				}
				else {
					System.out.println(file.getAbsolutePath()+"   doesnt exist");
				}
			//}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return hm;
	}
}
