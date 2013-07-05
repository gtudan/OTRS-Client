package de.tudan.otrsclient;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Gregor Tudan
 */
public class XSDTypeConverterTest {

	@Test
	public void testBase64() {
		Object base64 = XSDTypeConverter.convertXSDToObject("SsO2cmc=", "xsd:base64binary");
		assertThat(base64, is(instanceOf(String.class)));
		assertThat(base64.toString(), is("Jörg"));
	}
}
