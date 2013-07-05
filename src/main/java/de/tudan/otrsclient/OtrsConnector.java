package de.tudan.otrsclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.soap.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class OtrsConnector {

	final URL OTRS_URL;
	final String OTRS_USER;
	final String OTRS_PASS;

	private Logger log = LoggerFactory.getLogger(getClass());

	public OtrsConnector(String otrsUrl, String otrsUser, String otrsPass)
			throws MalformedURLException {
		super();
		OTRS_URL = new URL(otrsUrl);
		OTRS_USER = otrsUser;
		OTRS_PASS = otrsPass;
	}

	private SOAPMessage buildRPCMessage() throws SOAPException {
		// Create message
		MessageFactory mf = MessageFactory.newInstance();
		SOAPMessage msg = mf.createMessage();

		// Object for message parts
		SOAPPart sp = msg.getSOAPPart();

		SOAPEnvelope env = sp.getEnvelope();
		env.addNamespaceDeclaration("xsd", XMLConstants.W3C_XML_SCHEMA_NS_URI);
		env.addNamespaceDeclaration("xsi",
				XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
		env.addNamespaceDeclaration("soapenc",
				SOAPConstants.URI_NS_SOAP_ENCODING);
		env.addNamespaceDeclaration("env", SOAPConstants.URI_NS_SOAP_ENVELOPE);
		env.setEncodingStyle(SOAPConstants.URI_NS_SOAP_ENCODING);

		SOAPBody body = env.getBody();
		SOAPBodyElement dispatch = body.addBodyElement(new QName("/Core",
				"Dispatch"));
		dispatch.addChildElement("Username").addTextNode(OTRS_USER)
				.setAttribute("xsi:type", "xsd:string");
		dispatch.addChildElement("Password").addTextNode(OTRS_PASS)
				.setAttribute("xsi:type", "xsd:string");

		return msg;
	}

	private SOAPElement getDispatchBodyElement(SOAPMessage msg)
			throws SOAPException {
		return (SOAPElement) msg.getSOAPBody()
				.getChildElements(new QName("/Core", "Dispatch")).next();
	}

	private SOAPMessage prepareSOAPMessage(String object, String method,
	                                       Map<String, Object> params) throws SOAPException {
		SOAPMessage msg = this.buildRPCMessage();
		SOAPElement dispatch = this.getDispatchBodyElement(msg);

		dispatch.addChildElement("Object").addTextNode(object)
				.setAttribute("xsi:type", "xsd:string");
		dispatch.addChildElement("Method").addTextNode(method)
				.setAttribute("xsi:type", "xsd:string");

		String[] keys = params.keySet().toArray(new String[params.keySet().size()]);

		for (int i = 0; i < keys.length; i++) {
			String key = keys[i];

			dispatch.addChildElement("Param" + i + "_Name").addTextNode(key)
					.setAttribute("xsi:type", "xsd:string");

			// Check if we need an array
			if (params.get(key).getClass().isArray()) {
				SOAPElement element = new SoapArrayFactory().createSoapArray(
						"Param" + i + "_Value", Arrays.asList(params.get(key)));
				dispatch.addChildElement(element);
				// collections will also be encoded as array
			} else if (params.get(key) instanceof Collection<?>) {
				SOAPElement element = new SoapArrayFactory()
						.createSoapArray("Param" + i + "_Value",
								(Collection<?>) params.get(key));
				dispatch.addChildElement(element);
				// maps need special encoding as well
			} else if (params.get(key) instanceof Map<?, ?>) {
				SOAPElement element = new SoapMapFactory().createSoapMap("Param" + i + "_Value", (Map<?, ?>) params.get(key));
				dispatch.addChildElement(element);
				// or a simple node
			} else {
				String xsdType = XSDTypeConverter.simpleTypeForObject(params.get(key));
				String value = params.get(key).toString();
				dispatch.addChildElement("Param" + i + "_Value")
						.addTextNode(value).setAttribute("xsi:type", xsdType);
			}
		}
		return msg;
	}

	public SOAPMessage dispatchCall(String object, String method,
	                                Map<String, Object> params) throws SOAPException, IOException {
		SOAPMessage msg = prepareSOAPMessage(object, method, params);

		logMessage(msg);

		SOAPConnectionFactory scf = SOAPConnectionFactory.newInstance();
		SOAPConnection conn = scf.createConnection();
		SOAPMessage answerMsg = conn.call(msg, OTRS_URL);

		logMessage(answerMsg);
		return answerMsg;
	}

	private void logMessage(SOAPMessage msg) throws IOException,
			SOAPException {
		if (log.isDebugEnabled()) {
			// This will write the request to log
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			msg.writeTo(outputStream);
			byte[] byteArray = outputStream.toByteArray();
			String soapMsg = new String(byteArray, StandardCharsets.UTF_8);
			log.debug(soapMsg);
		}
	}

	public SOAPMessage dispatchCall(String object, String method)
			throws SOAPException, IOException {
		return this.dispatchCall(object, method, new HashMap<String, Object>());
	}
}
