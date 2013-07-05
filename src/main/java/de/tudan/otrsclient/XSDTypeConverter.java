package de.tudan.otrsclient;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility for converting java datatypes to xml-schema and back
 *
 * @author Gregor Tudan
 */
public class XSDTypeConverter {

	private static Logger log = LoggerFactory.getLogger(XSDTypeConverter.class);

	/**
	 * Returns the xsd simple Type for the given Object
	 * Only supports simple types!
	 * <br/>
	 * defaults to xsd:string
	 *
	 * @param obj object to get type for
	 * @return xsd:simple type
	 */
	public static String simpleTypeForObject(Object obj) {
		if (obj instanceof String) {
			return "xsd:string";
		} else if (obj instanceof Long) {
			return "xsd:long";
		} else if (obj instanceof Integer) {
			return "xsd:integer";
		} else if (obj instanceof Boolean) {
			return "xsd:boolean";
		} else if (obj instanceof Date) {
			return "xsd:date";
		} else if (obj instanceof Float) {
			return "xsd:float";
		} else {
			return "xsd:string";
		}
	}

	/**
	 * Given a string and an xsd-Type, this will convert the string back to a java object
	 *
	 * @param obj     the string to convert
	 * @param xsdType the xsd-type
	 * @return java object matching the given type
	 */
	public static Object convertXSDToObject(String obj, String xsdType) {
		xsdType = xsdType.toLowerCase();

		switch (xsdType) {
			case "xsd:string":
				return obj;
			case "xsd:long":
				return Long.valueOf(obj);
			case "xsd:integer":
				return Integer.valueOf(obj);
			case "xsd:int":
				return Integer.valueOf(obj);
			case "xsd:float":
				return Float.valueOf(obj);
			case "xsd:boolean":
				return Boolean.valueOf(obj);
			case "xsd:decimal":
				return Long.valueOf(obj);
			case "xsd:date":
				try {
					return new SimpleDateFormat("yyyy-MM-dd").parse(obj);
				} catch (ParseException e) {
					log.error("Failed to parse date: {}", obj, e);
				}
				break;
			case "xsd:base64binary":
				return new String(Base64.decodeBase64(obj.getBytes()), StandardCharsets.UTF_8);
			default:
				log.warn("Could not convert data type {}.", xsdType);
				break;
		}
		return obj;
	}
}