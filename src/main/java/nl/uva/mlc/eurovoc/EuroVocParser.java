package nl.uva.mlc.eurovoc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 *
 * @author  Mostafa Dehghani
 */

public abstract class EuroVocParser {
    
    
    public void fileReader(File mainFile) {
        File[] files = mainFile.listFiles();
        for(File file:files){
            if(file.isDirectory()){
                fileReader(file);
            }
            else{
                if(file.getName().startsWith("jrc")&& file.getName().endsWith(".xml"))
                                fileParser(file);
            }
        }
    }
    
    private void fileParser(File file){
               
                XPath xpath = null;
                Document document = null;
                try {
                    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();   
                    docFactory.setNamespaceAware(true);
                    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                    document = docBuilder.parse(file);
                    XPathFactory xpathfactory = XPathFactory.newInstance();
                    xpath = xpathfactory.newXPath();
                    
                } catch (ParserConfigurationException ex) {
                    Logger.getLogger(EuroVocParser.class.getName()).log(Level.SEVERE, null, ex);
                    System.err.println("EROOR IN: " + file.getPath());
                    return;
                } catch (SAXException ex) {
                    Logger.getLogger(EuroVocParser.class.getName()).log(Level.SEVERE, null, ex);
                    System.err.println("EROOR IN: " + file.getPath());
                    return;
                } catch (IOException ex) {
                    Logger.getLogger(EuroVocParser.class.getName()).log(Level.SEVERE, null, ex);
                    System.err.println("EROOR IN: " + file.getPath());
                    return;
                }
                    
                    try {
                        XPathExpression idExpr = xpath.compile("/TEI.2");
                        XPathExpression nExpr = xpath.compile("/TEI.2");
                        XPathExpression langExpr = xpath.compile("/TEI.2");
                        XPathExpression creationDateExpr = xpath.compile("//teiHeader");
                        XPathExpression titleExpr = xpath.compile("//title[2]");
                        XPathExpression urlExpr = xpath.compile("//xref");
                        XPathExpression noteExpr = xpath.compile("//note");
                        XPathExpression classExpr = xpath.compile("//classCode");
                        XPathExpression textExpr = xpath.compile("//p");
                           
                        String id = ((Node)idExpr.evaluate(document, XPathConstants.NODE)).getAttributes().getNamedItem("id").getTextContent();
                        String n = ((Node)nExpr.evaluate(document, XPathConstants.NODE)).getAttributes().getNamedItem("n").getTextContent();
                        String lang = ((Node)langExpr.evaluate(document, XPathConstants.NODE)).getAttributes().getNamedItem("lang").getTextContent();
                        String creationDate = ((Node)creationDateExpr.evaluate(document, XPathConstants.NODE)).getAttributes().getNamedItem("date.created").getTextContent();
                        String title = (String)titleExpr.evaluate(document, XPathConstants.STRING);
                        String url = (String)urlExpr.evaluate(document, XPathConstants.STRING);
                        String note = (String)noteExpr.evaluate(document, XPathConstants.STRING);
                        NodeList classesNodes = (NodeList)classExpr.evaluate(document, XPathConstants.NODESET);
                        ArrayList<String> classes = new ArrayList<>();
                        for (int i = 0; i < classesNodes.getLength(); i++) {
                            classes.add(classesNodes.item(i).getTextContent());
                        }
                        NodeList textNodes = (NodeList)textExpr.evaluate(document, XPathConstants.NODESET);
                        String text = "";
                        for (int i = 0; i < textNodes.getLength(); i++) {
                            text += textNodes.item(i).getTextContent()  + "\n";
                        }
                        EuroVocDoc doc = new EuroVocDoc(id, n, lang, creationDate, title, url, note, text.trim(), classes);
                        doSomeAction(doc);
                    } catch (XPathExpressionException ex) {
                        Logger.getLogger(EuroVocParser.class.getName()).log(Level.SEVERE, null, ex);
                        System.err.println("EROOR IN: " + file.getPath());
                        return;
                    }
    }
    public abstract void doSomeAction(EuroVocDoc doc); 
}
