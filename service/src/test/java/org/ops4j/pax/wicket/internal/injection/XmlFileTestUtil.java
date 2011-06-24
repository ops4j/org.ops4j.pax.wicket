package org.ops4j.pax.wicket.internal.injection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlFileTestUtil {

    public static Document loadXml(String fileName) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(fileName);
        return doc;
    }

    public static Element loadFirstElementThatMatches(String element, String filename) throws Exception {
        Document doc = loadXml(filename);
        return (Element) doc.getElementsByTagName(element).item(0);
    }

}
