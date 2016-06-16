package parser;


import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
	
public class ParserManager {

	  public Document document=null;
	  
	
	  public ParserManager(String filePath, boolean normalize){
		  
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder;
			
			try {
				  documentBuilder = documentBuilderFactory.newDocumentBuilder();
				  document = documentBuilder.parse(new File(filePath));
				  
				  if(normalize){
					  document.getDocumentElement().normalize();
				  }
				  
			} catch (ParserConfigurationException e) {
				 System.out.println(e.getMessage());
			} catch (SAXException e) {
				 System.out.println(e.getMessage());
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
  
	  }
	  
	  public ParserManager(File file, boolean normalize){
		  
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder;
			
			try {
				  documentBuilder = documentBuilderFactory.newDocumentBuilder();
				  document = documentBuilder.parse(file);
				  
				  if(normalize){
					  document.getDocumentElement().normalize();
				  }
				  
			} catch (ParserConfigurationException e) {
				 System.out.println(e.getMessage());
			} catch (SAXException e) {
				 System.out.println(e.getMessage());
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		  
	  }
	  
	
	  
	  /**
	   * 
	   * @param rootElement
	   * @param childElement
	   * @return
	   */
	  public Element appendElement(Element rootElement, Element childElement){
		   return (Element)rootElement.appendChild(childElement);
	  }
	  
	  /**
	   * 
	   * @param rootElement
	   * @param childElement
	   * @return
	   */
	  public Node appendElement2(Element rootElement, Element childElement){
		   return rootElement.appendChild(childElement);
	  }
	  
	  
	  public Node  appendTextNode(Element rootElement,String nodeName,String textValue){
	  
		  	Element textNode = document.createElement(nodeName);
		  	textNode.appendChild(document.createTextNode(textValue));
		  	rootElement.appendChild(textNode);
		  
		  	return textNode;
	  }


	 

}
