package de.tudan.otrsclient;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class OtrsSoapMessageParser {

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
		Element el = getDispatchResponse(msg.getSOAPPart().getEnvelope().getBody());
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

		Element el = getDispatchResponse(msg.getSOAPPart().getEnvelope().getBody());
		NodeList nl = el.getChildNodes();

		for (int i = 0; i < (nl.getLength() / 2); i++) {
			Node valueNode = nl.item(i * 2 + 1);
			String key = nl.item(i * 2).getTextContent().trim();

			Object value = this.nodeToObject(valueNode);

			map.put(key, value);
		}

		return map;
	}
	
	private Element getDispatchResponse(SOAPBody body) throws SOAPException{
		Element dispatchResponse = null;
		try {
			Document doc = body.extractContentAsDocument();
			dispatchResponse = doc.getDocumentElement();
		} catch (SOAPException e){
			//Workaround for CustomerUserDataGet because it returns more child elements in SOAP:Body
			@SuppressWarnings("unchecked")
			Iterator<Element> it = body.getChildElements();
			while(it.hasNext() && dispatchResponse == null){
				Element tmp = it.next();
				if(tmp.getNodeName().equals("DispatchResponse")){
					dispatchResponse = tmp;
				}
			}
			//If the DispatchResponse can still not been found throw the SOAPException
			if(dispatchResponse == null){
				throw e;
			}
		}
		
		return dispatchResponse;
	}
}
