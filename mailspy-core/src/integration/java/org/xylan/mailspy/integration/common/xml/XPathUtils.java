package org.xylan.mailspy.integration.common.xml;

import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

public class XPathUtils {

    private XPathUtils() {}

    @SneakyThrows
    public static String getValueByXPath(String document, String xpath) {
        Document xmlDocument = DocumentBuilderFactory.newInstance()
            .newDocumentBuilder()
            .parse(IOUtils.toInputStream(document, "UTF-8"));
        return (String) XPathFactory.newInstance()
            .newXPath()
            .compile(xpath)
            .evaluate(xmlDocument, XPathConstants.STRING);
    }

}
