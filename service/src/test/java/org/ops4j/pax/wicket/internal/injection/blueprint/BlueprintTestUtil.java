package org.ops4j.pax.wicket.internal.injection.blueprint;

import org.ops4j.pax.wicket.internal.injection.XmlFileTestUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class BlueprintTestUtil {

    public static Document loadSpringXml() throws Exception {
        return XmlFileTestUtil.loadXml("src/test/resources/blueprint.xml");
    }

    public static Element loadFirstElementThatMatches(String element) throws Exception {
        return XmlFileTestUtil.loadFirstElementThatMatches(element, "src/test/resources/blueprint.xml");
    }

}
