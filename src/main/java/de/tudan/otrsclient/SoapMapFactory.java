package de.tudan.otrsclient;

import org.w3c.dom.DOMException;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import java.util.Map;

/**
 * Encodes maps the "otrs-way".
 *
 * @author tschechniker
 * @author Gregor Tudan
 */
class SoapMapFactory {
	protected SOAPElement createSoapMap(String name, Map<?, ?> values)
			throws DOMException, SOAPException {
		SOAPElement element = SOAPFactory.newInstance().createElement(name);
		element.addNamespaceDeclaration("ns2", "http://xml.apache.org/xml-soap");
		element.setAttribute("xsi:type", "ns2:Map");
		SOAPElement item = SOAPFactory.newInstance().createElement("item");
		for (Map.Entry<?, ?> entry : values.entrySet()) {
			item.addChildElement("key").addTextNode(entry.getKey().toString()).setAttribute("xsi:type", "xsd:string");
			SOAPElement valueElement = item.addChildElement("value");
			if (entry.getValue() == null) {
				valueElement.setAttribute("xsi:nil", "true");
			} else {
				valueElement.addTextNode(entry.getValue().toString()).setAttribute("xsi:type", "xsd:string");
			}
		}
		element.addChildElement(item);
		return element;
	}
}
