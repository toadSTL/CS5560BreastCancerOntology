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
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.FileReader;

public class XMLReaderForAbstracts4 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String titles = "";
		for (int i = 1; i < 11; i++) {
			try {

				File file = new File("data_breast_cancer/abstracts/" + (i) + ".xml");
				PrintWriter writer = new PrintWriter("data_breast_cancer/abstracts/temp" + (i) + ".txt", "UTF-8");//ADDED
				//writer.write(str);


				if (file.exists()) {
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					DocumentBuilder db = dbf.newDocumentBuilder();
					Document doc = db.parse(file);
					doc.getDocumentElement().normalize();
					System.out.println("Root element " + doc.getDocumentElement().getNodeName());
					NodeList nodeLst = doc.getElementsByTagName("PubmedArticle");

					for (int s = 0; s < nodeLst.getLength(); s++) {

						Node fstNode = nodeLst.item(s);
						//  System.out.println(fstNode.getNodeValue());
						if (fstNode.getNodeType() == Node.ELEMENT_NODE) {

							Element fstElmnt = (Element) fstNode;
							// System.out.println(fstElmnt.getNodeValue());
							NodeList fstNmElmntLst = fstElmnt.getElementsByTagName("MedlineCitation");
							Element fstNmElmnt = (Element) fstNmElmntLst.item(0);
							NodeList articleNodeList = fstNmElmnt.getElementsByTagName("Article");
							String IDs = null;
							for (int t = 0; t < articleNodeList.getLength(); t++) {
								Element articleElement = (Element) articleNodeList.item(t);
								NodeList abstractNodeList = articleElement.getElementsByTagName("Abstract");
								if (abstractNodeList.item(0).getNodeType() == Node.ELEMENT_NODE) {
									Element abstractElement = (Element) abstractNodeList.item(t);
									NodeList abstract2 = abstractElement.getElementsByTagName("AbstractText");
									for (int t1 = 0; t1 < fstNmElmntLst.getLength(); t1++) {
										Element abstrElement = (Element) abstract2.item(t1);
										NodeList fstNm = abstrElement.getChildNodes();
										titles += articleElement.getElementsByTagName("ArticleTitle").item(0).getNodeValue();
										titles += "\n";
										System.out.println((((Node) fstNm.item(0)).getNodeValue()));
										writer.println((((Node) fstNm.item(0)).getNodeValue())); //ADDED

									}
								}
							}
							writer.close(); //ADDED
							System.out.println("Done");
						}
					}

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

System.out.println(titles);

		for (int i = 1; i < 11; i++) {
			FileReader fr = null;
			BufferedReader br = null;
			PrintWriter writer = null;
			try {
				//br = new BufferedReader(new FileReader(FILENAME));
				fr = new FileReader("data_breast_cancer/abstracts/temp" + (i) + ".txt");
				br = new BufferedReader(fr);
				//File file = new File("new_data_breast_cancer/abstracts/" + (i) + ".xml");
				writer = new PrintWriter("data_breast_cancer/abstracts/abs_" + (i) + ".txt", "UTF-8");

				String currentLine;

				while ((currentLine = br.readLine()) != null) {
					//System.out.println(currentLine);
					String[] sentences = currentLine.split("\\. ");
					//System.out.println(sentences[0]);
					for(int j = 0; j < sentences.length; j++) {
						if(j == (sentences.length-1)) {
							writer.println(sentences[j]);
						}else {writer.println(sentences[j]+".");}
					}
				}
			} catch (IOException e) {

				e.printStackTrace();

			} finally {

				try {

					if (br != null)
						br.close();

					if (fr != null)
						fr.close();

					if(writer != null)
						writer.close();
			} catch (IOException ex) {

					ex.printStackTrace();

				}

			}
		}

	}
}
