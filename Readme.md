[![Build Status](https://travis-ci.org/gtudan/OTRS-Client.svg?branch=master)](https://travis-ci.org/gtudan/OTRS-Client)

Accessing OTRS SOAP using Java/Groovy
=====================================

Being interested in Webservices, I was searching for something to play with in order to get a bit more practise and found OTRS at work. I was excited and wanted to give it a try, but soon found, that the interface OTRS was offering had little to do with Webservices. It is more like RPC using the SOAP protocol, basic elements like a predefined interface (WSDL) are missing. This makes all the frameworks for Webservices in Java practically useless. After googling around, I found [a post from Joko Sastriawan](http://sastriawan.blogspot.com/2010/01/using-javaxxmlsoap-to-access-otrs-soap.html) explaining how to build SOAP-Messages using the standard JDK methods.

I gave it a try and came up with a generic Java utility for creating SOAP Messages for OTRS. While is written in pure Java, it is intended to be used with something dynamic like Groovy and comes pretty close to the feeling of the original SOAP::Lite client. Watch this:

	// Prepare Connection
	def otrsConn =  new OtrsConnector('https://example.org/otrs/rpc.pl', 'soapUser', 'soapPass')

	// Find all open tickets
	def params = [Result:'ARRAY', Limit:100, UserID:1, StateType:['open']]
	def msg = otrsConn.dispatchCall("TicketObject", "TicketSearch", params)
	String[] result = new OtrsSoapMessageParser().nodesToArray(msg)

This will give you an array of TicketIDs, that can be used for more sophisticated stuff. Let’s print the body of the last Article for a Ticket:

	def msg2 = otrsConn.dispatchCall("TicketObject", "ArticleFirstArticle", [TicketID:0815])
	def result2 = new OtrsSoapMessageParser().nodesToMap(msg2)   
	 
	// Accessing properties is easy using groovy, since this is a map
	def body = result2['Body']     // or result2.Body
	 
	// Body comes base64 encoded, we need some magic here...
	println new String(body.decodeBase64(),'UTF-8')

The library is licensed under LGPG v3.0
