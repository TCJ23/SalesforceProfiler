package file;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import javax.xml.xpath.XPath.*;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class FileManager {
	
	public File directory;
	public File[] files;
	
	
	public FileManager(String directoryPath){
		
	    this.directory = new File(directoryPath);

	}
	
	public File[] getFileList(){
		
	  this.files = this.directory.listFiles();
	  
	  return files;
	}
	
	public String getFilePath(File f){
		
		return f.getAbsolutePath();
	}
	
	
	public static void saveXmlFile(Document doc,File f){
		try {
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(f);
			
			sortXML(doc);
			
			doc.normalize();
		    doc.normalizeDocument();
		    
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		    
			cleanXMLRows(doc);

		    transformer.transform(source, result);
			
			System.out.println("File saved!");
			
		} catch (TransformerException e) {
		
			System.out.println(e.getMessage());
		}

	}
	
	public static void cleanXMLRows(Document doc){
		XPathFactory xpathFactory = XPathFactory.newInstance();
		
		// XPath to find empty text nodes.
		XPathExpression xpathExp;
		
		try {
			xpathExp = xpathFactory.newXPath().compile("//text()[normalize-space() = '']");			
			NodeList emptyTextNodes = (NodeList)xpathExp.evaluate(doc, XPathConstants.NODESET);
			
			// Remove each empty text node from document.
			for (int i = 0; i < emptyTextNodes.getLength(); i++) {
			    Node emptyTextNode = emptyTextNodes.item(i);
			    emptyTextNode.getParentNode().removeChild(emptyTextNode);
			}
			
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}  
		
	}
	
	public static void sortXML(Document doc){
		Node allNodes = doc.getFirstChild();
		
		sortChildNodes(allNodes, false, 1, null);
	}
	
	public static void sortChildNodes(Node node, boolean descending, int depth, Comparator comparator) {

        List nodes = new ArrayList();
        NodeList childNodeList = node.getChildNodes();
        if (depth > 0 && childNodeList.getLength() > 0) {
           for (int i = 0; i < childNodeList.getLength(); i++) {
                Node tNode = childNodeList.item(i);
                sortChildNodes(tNode, descending, depth - 1,
                               comparator);
                // Remove empty text nodes
                if ((!(tNode instanceof Text)) || 
                		(tNode instanceof Text && 
                		((Text) tNode).getTextContent().trim().length() > 1)) {    
                	
                   nodes.add(tNode);
                }
           }
           Comparator comp = (comparator != null) ? comparator: new DefaultNodeNameComparator();
           if (descending)
           {
            //if descending is true, get the reverse ordered comparator
                Collections.sort(nodes, Collections.reverseOrder(comp));
           } else {
                Collections.sort(nodes, comp);
           }

          for (Iterator iter = nodes.iterator(); iter.hasNext();) {
                Node element = (Node) iter.next();
                node.appendChild(element);
          }
        }

}
	
//	public void writeFile(String value){
//	    String PATH = "../";
//	    String directoryName = PATH.concat(this.getClass().getName());
//
//	    File directory = new File(String.valueOf(directoryName));
//	    if (! directory.exists()){
//	        directory.mkdir();
//	    }
//
//	    File 
//	    try{
//	        FileWriter fw = new FileWriter(file.getAbsoluteFile());
//	        BufferedWriter bw = new BufferedWriter(fw);
//	        bw.write(value);
//	        bw.close();
//	    }
//	    catch (IOException e){
//	        e.printStackTrace();
//	        System.exit(-1);
//	    }
//	}
	
	
}

class DefaultNodeNameComparator implements Comparator {

         public int compare(Object arg0, Object arg1) {
                 return ((Node) arg0).getNodeName().compareTo(
                                 ((Node) arg1).getNodeName());
         }
 
 }
