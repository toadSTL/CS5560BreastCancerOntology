import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class AbsRet2 {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        boolean keep = false;
        int numOccurBCfull = 0;
        int numOccurBC = 0;
        int numOccurBCa = 0;
        int numOccurTNBC = 0;
        int numOccurAnyBC = 0;
        int len = 0;

        int nOTitleB = 0;
        int nOTitleC = 0;

        int k = 1;
        String titles = "";
        for (int i = 1; i < 751; i++) {
            try {

                File file = new File("abs/xml/" + (i) + ".xml");
                PrintWriter writer = new PrintWriter("abs/text/" + (i) + ".txt", "UTF-8");//ADDED
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
                            Node pmidNode = fstNmElmnt.getElementsByTagName("PMID").item(0);
                            System.out.println(pmidNode.getTextContent());
                            NodeList articleNodeList = fstNmElmnt.getElementsByTagName("Article");
                            String IDs = null;
                            for (int t = 0; t < articleNodeList.getLength(); t++) {
                                Element articleElement = (Element) articleNodeList.item(t);
                                NodeList abstractNodeList = articleElement.getElementsByTagName("Abstract");
                                if(abstractNodeList.item(0)!=null) {
                                    if (abstractNodeList.item(0).getNodeType() == Node.ELEMENT_NODE) {
                                        Element abstractElement = (Element) abstractNodeList.item(t);
                                        NodeList abstract2 = abstractElement.getElementsByTagName("AbstractText");
                                        for (int t1 = 0; t1 < fstNmElmntLst.getLength(); t1++) {
                                            Element abstrElement = (Element) abstract2.item(t1);
                                            NodeList fstNm = abstrElement.getChildNodes();
//										titles += articleElement.getElementsByTagName("ArticleTitle").item(0).getTextContent();
//										titles += " - "+pmidNode.getTextContent()+"\n";
                                            if(((Node) fstNm.item(0))!=null&&((Node) fstNm.item(0)).getNodeValue()!=null) {
                                                len = (((Node) fstNm.item(0)).getNodeValue()).length();
                                                numOccurBCfull = (((((Node) fstNm.item(0)).getNodeValue()).length() - (((Node) fstNm.item(0)).getNodeValue()).replaceAll("(?i)breast cancer", "").length()) / "breast cancer".length());
                                                numOccurBC = (((((Node) fstNm.item(0)).getNodeValue()).length() - (((Node) fstNm.item(0)).getNodeValue()).replaceAll("(?i)BC", "").length()) / "BC".length());
                                                numOccurBCa = (((((Node) fstNm.item(0)).getNodeValue()).length() - (((Node) fstNm.item(0)).getNodeValue()).replaceAll("(?i)BCa", "").length()) / "BCa".length());
                                                numOccurTNBC = (((((Node) fstNm.item(0)).getNodeValue()).length() - (((Node) fstNm.item(0)).getNodeValue()).replaceAll("(?i)TNBC", "").length()) / "TNBC".length());
                                                numOccurAnyBC = numOccurBCfull + Math.max(Math.max(numOccurBC, numOccurBCa), Math.max(numOccurBC, numOccurTNBC));

                                                nOTitleB += (articleElement.getElementsByTagName("ArticleTitle").item(0).getTextContent().length() - articleElement.getElementsByTagName("ArticleTitle").item(0).getTextContent().replaceAll("(?i)breast", "").length()) / "breast".length();
                                                nOTitleB += (articleElement.getElementsByTagName("ArticleTitle").item(0).getTextContent().length() - articleElement.getElementsByTagName("ArticleTitle").item(0).getTextContent().replaceAll("(?i)mammary", "").length()) / "mammary".length();
                                                nOTitleB += (articleElement.getElementsByTagName("ArticleTitle").item(0).getTextContent().length() - articleElement.getElementsByTagName("ArticleTitle").item(0).getTextContent().replaceAll("(?i)mammogram", "").length()) / "mammogram".length();
                                                nOTitleB += (articleElement.getElementsByTagName("ArticleTitle").item(0).getTextContent().length() - articleElement.getElementsByTagName("ArticleTitle").item(0).getTextContent().replaceAll("(?i)ductal", "").length()) / "ductal".length();

                                                nOTitleC += (articleElement.getElementsByTagName("ArticleTitle").item(0).getTextContent().length() - articleElement.getElementsByTagName("ArticleTitle").item(0).getTextContent().replaceAll("(?i)malignant", "").length()) / "malignant".length();
                                                nOTitleC += (articleElement.getElementsByTagName("ArticleTitle").item(0).getTextContent().length() - articleElement.getElementsByTagName("ArticleTitle").item(0).getTextContent().replaceAll("(?i)chemotherapy", "").length()) / "chemotherapy".length();
                                                nOTitleC += (articleElement.getElementsByTagName("ArticleTitle").item(0).getTextContent().length() - articleElement.getElementsByTagName("ArticleTitle").item(0).getTextContent().replaceAll("(?i)carcinoma", "").length()) / "carcinoma".length();
                                                nOTitleC += (articleElement.getElementsByTagName("ArticleTitle").item(0).getTextContent().length() - articleElement.getElementsByTagName("ArticleTitle").item(0).getTextContent().replaceAll("(?i)carcinogenesis", "").length()) / "carcinogenesis".length();
                                                nOTitleC += (articleElement.getElementsByTagName("ArticleTitle").item(0).getTextContent().length() - articleElement.getElementsByTagName("ArticleTitle").item(0).getTextContent().replaceAll("(?i)tumor", "").length()) / "tumor".length();
                                                nOTitleC += (articleElement.getElementsByTagName("ArticleTitle").item(0).getTextContent().length() - articleElement.getElementsByTagName("ArticleTitle").item(0).getTextContent().replaceAll("(?i)tumour", "").length()) / "tumour".length();
                                                nOTitleC += (articleElement.getElementsByTagName("ArticleTitle").item(0).getTextContent().length() - articleElement.getElementsByTagName("ArticleTitle").item(0).getTextContent().replaceAll("(?i)metastasis", "").length()) / "metastasis".length();
                                                nOTitleC += (articleElement.getElementsByTagName("ArticleTitle").item(0).getTextContent().length() - articleElement.getElementsByTagName("ArticleTitle").item(0).getTextContent().replaceAll("(?i)metastases", "").length()) / "metastases".length();
                                                nOTitleC += (articleElement.getElementsByTagName("ArticleTitle").item(0).getTextContent().length() - articleElement.getElementsByTagName("ArticleTitle").item(0).getTextContent().replaceAll("(?i)cancer", "").length()) / "cancer".length();
                                                nOTitleC += (articleElement.getElementsByTagName("ArticleTitle").item(0).getTextContent().length() - articleElement.getElementsByTagName("ArticleTitle").item(0).getTextContent().replaceAll("(?i)neoplasm", "").length()) / "neoplasm".length();
                                                nOTitleC += (articleElement.getElementsByTagName("ArticleTitle").item(0).getTextContent().length() - articleElement.getElementsByTagName("ArticleTitle").item(0).getTextContent().replaceAll("(?i)neoplasms", "").length()) / "neoplasms".length();


                                                System.out.println(articleElement.getElementsByTagName("ArticleTitle").item(0).getTextContent());
                                                System.out.println("Instance(s) of 'breast': " + nOTitleB);
                                                System.out.println("Instance(s) of 'cancer': " + nOTitleC);

                                                System.out.println(numOccurBCfull);
                                                System.out.println(numOccurBC);
                                                System.out.println(numOccurBCa);
                                                System.out.println(numOccurTNBC);
                                                System.out.println(numOccurAnyBC);


                                                if ((((Node) fstNm.item(0)).getNodeValue()).length() > 500 && numOccurAnyBC >= 1 && nOTitleB >=1 && nOTitleC >= 1) {
                                                    keep = true;
                                                    PrintWriter inclWriter = new PrintWriter("abs/incl/" + (k) + ".txt", "UTF-8");
                                                    inclWriter.println((((Node) fstNm.item(0)).getNodeValue()));
                                                    inclWriter.close();
                                                    titles += k + " - " + pmidNode.getTextContent() + " - ";
                                                    titles += articleElement.getElementsByTagName("ArticleTitle").item(0).getTextContent() + "\n";
                                                    k++;
                                                }

                                                System.out.println(keep);
                                                System.out.println(len);
                                                System.out.println((((Node) fstNm.item(0)).getNodeValue()));

                                                len = 0;
                                                numOccurBCfull = 0;
                                                numOccurBC = 0;
                                                numOccurBCa = 0;
                                                numOccurTNBC = 0;
                                                numOccurAnyBC = 0;
                                                nOTitleB = 0;
                                                nOTitleC = 0;
                                                keep = false;
                                                writer.println((((Node) fstNm.item(0)).getNodeValue())); //ADDED
                                            }
                                        }
                                    }
                                }else{

                                }
                            }
                            writer.close(); //ADDED
                            System.out.println("Done");
                        }
                    }

                }


//			}

            } catch (DOMException |
                    ParserConfigurationException |
                    SAXException |
                    IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println(titles);
        try {
            PrintWriter pw = new PrintWriter("meta/Doc-PubMedID-Title.txt", "UTF-8");
            pw.write(titles);
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
