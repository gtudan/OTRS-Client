package de.tudan.otrsclient;

import org.w3c.dom.DOMException;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import java.util.Collection;

/**
 * Utility to create SoapEncoded Arrays from Collections
 *
 * @author Gregor Tudan
 */
public class SoapArrayFactory {

	protected <T> SOAPElement createSoapArray(String name, Collection<T> values)
			throws DOMException, SOAPException {
		SOAPElement element = SOAPFactory.newInstance().createElement(name);

		// Check the type of the array and default to string if empty
		String type;
		if (values.size() != 0) {
			type = XSDTypeConverter.simpleTypeForObject(values.iterator().next());
		} else {
			type = "xsi:string";
		}

		element.setAttribute("soapenc:arrayType", type + "[" + values.size() + "]");
		element.setAttribute("xsi:type", "soapenc:Array");

		for (T s : values) {
			element.addChildElement("item").addTextNode(s.toString()).setAttribute("xsi:type", type);
		}

		return element;
	}
}
