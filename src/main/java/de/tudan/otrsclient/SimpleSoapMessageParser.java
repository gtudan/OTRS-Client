package de.tudan.otrsclient;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleSoapMessageParser {

	private Object nodeToObject(Node node) {
		Node xsdTypeNode = node.getAttributes().getNamedItemNS(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type");

		Object value;
		if (xsdTypeNode != null) {
			value = XSDTypeConverter.convertXSDToObject(node.getTextContent().trim(), xsdTypeNode.getTextContent().trim());
		} else {
			value = node.getTextContent().trim();
		}

		return value;
	}

	public Object[] nodesToArray(SOAPMessage msg) throws SOAPException {
		Document doc = msg.getSOAPPart().getEnvelope().getBody().extractContentAsDocument();
		Element el = doc.getDocumentElement();
		NodeList nl = el.getChildNodes();

		Object[] results = new Object[nl.getLength()];

		for (int i = 0; i < nl.getLength(); i++) {
			results[i] = this.nodeToObject(nl.item(i));
		}
		return results;
	}

	public List<?> nodesToList(SOAPMessage msg) throws SOAPException {
		return Arrays.asList(this.nodesToArray(msg));
	}

	public Map<String, Object> nodesToMap(SOAPMessage msg) throws SOAPException {
		Map<String, Object> map = new HashMap<>();

		Document doc = msg.getSOAPPart().getEnvelope().getBody().extractContentAsDocument();
		Element el = doc.getDocumentElement();
		NodeList nl = el.getChildNodes();

		for (int i = 0; i < (nl.getLength() / 2); i++) {
			Node valueNode = nl.item(i * 2 + 1);
			String key = nl.item(i * 2).getTextContent().trim();

			Object value = this.nodeToObject(valueNode);

			map.put(key, value);
		}

		return map;
	}
}
