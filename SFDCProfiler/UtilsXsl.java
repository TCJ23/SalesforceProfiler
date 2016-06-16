package com.sniff.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.SerializerFactory;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * This example shows how to chain a series of transformations by
 * piping SAX events from one Transformer to another. Each Transformer
 * operates as a SAX2 XMLFilter/XMLReader.
 */
public class UtilsXsl
{
    public static void main(String[] args)
            throws TransformerException, TransformerConfigurationException,
            SAXException, IOException
    {
        // Instantiate  a TransformerFactory.
        TransformerFactory tFactory = TransformerFactory.newInstance();
        // Determine whether the TransformerFactory supports The use uf SAXSource
        // and SAXResult
        if (tFactory.getFeature(SAXSource.FEATURE) && tFactory.getFeature(SAXResult.FEATURE)) {
            // Cast the TransformerFactory to SAXTransformerFactory.
            SAXTransformerFactory saxTFactory = ((SAXTransformerFactory) tFactory);
            // Create an XMLFilter for each stylesheet.
            XMLFilter xmlFilter = saxTFactory.newXMLFilter(new StreamSource("C:\\Users\\Gian\\Desktop\\config.xsl"));

            // Create an XMLReader.
            XMLReader reader = XMLReaderFactory.createXMLReader();

            // xmlFilter uses the XMLReader as its reader.
            xmlFilter.setParent(reader);

            // xmlFilter3 outputs SAX events to the serializer.
            java.util.Properties xmlProps = OutputPropertiesFactory.getDefaultMethodProperties("xml");
            xmlProps.setProperty("indent", "yes");
            xmlProps.setProperty("standalone", "no");

            FileOutputStream fileOutputStream = null;
            File file = null;

            try {

                file = new File("C:\\Users\\Gian\\Desktop\\target.profile");
                file.createNewFile();
                fileOutputStream = new FileOutputStream(file);

                Serializer serializer = SerializerFactory.getSerializer(xmlProps);
                serializer.setOutputStream(fileOutputStream);

                xmlFilter.setContentHandler(serializer.asContentHandler());
                xmlFilter.parse(new InputSource("C:\\Users\\Gian\\Desktop\\Enterprise - Area Channel Sales Manager.profile"));

            } catch(IOException ex){

            } finally {
                fileOutputStream.close();
            }
        }
    }
}

