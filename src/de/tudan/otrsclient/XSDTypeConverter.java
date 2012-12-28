package de.tudan.otrsclient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        if (xsdType.equals("xsd:string")) {
            return obj;
        } else if (xsdType.equals("xsd:long")) {
            return Long.valueOf(obj);
        } else if (xsdType.equals("xsd:integer")) {
            return Integer.valueOf(obj);
        } else if (xsdType.equals("xsd:int")) {
            return Integer.valueOf(obj);
        } else if (xsdType.equals("xsd:float")) {
            return Float.valueOf(obj);
        } else if (xsdType.equals("xsd:boolean")) {
            return Boolean.valueOf(obj);
        } else if (xsdType.equals("xsd:decimal")) {
            return Long.valueOf(obj);
        } else if (xsdType.equals("xsd:date")) {
            try {
                return new SimpleDateFormat("yyyy-MM-dd").parse(obj);
            } catch (ParseException e) {
                log.error("Failed to parse date: {}", obj, e);
            }
        } else if (xsdType.equals("xsd:base64binary")) {
            return javax.xml.bind.DatatypeConverter.printBase64Binary(obj.getBytes());
        } else {
            log.warn("Could not convert data type {}.", xsdType);
        }
        return obj;
    }
}
