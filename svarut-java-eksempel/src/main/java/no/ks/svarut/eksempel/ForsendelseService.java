package no.ks.svarut.eksempel;

import no.ks.svarut.services.ForsendelsesServiceV4;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.cxf.ws.addressing.WSAddressingFeature;

import java.util.HashMap;
import java.util.Map;

public class ForsendelseService {

	private final static String USERNAME = "brukernavn";
	private final static String PASSORD = "passord";
	private final static String SERVICE_URL = "https://test.svarut.ks.no/tjenester/forsendelseservice/ForsendelsesServiceV4";
	private final static String PROPERTY_MTOM = "mtom-enabled";

	public static ForsendelsesServiceV4 create() {
		Map<String,Object> props = new HashMap<String, Object>();
		props.put("mtom-enabled", Boolean.TRUE);

		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(ForsendelsesServiceV4.class);
		factory.setAddress(SERVICE_URL);
		factory.setUsername(USERNAME);
		factory.setPassword(PASSORD);
		factory.setProperties(getProperties());
		factory.getFeatures().add(new WSAddressingFeature());
		ForsendelsesServiceV4 serviceV4 = (ForsendelsesServiceV4) factory.create();
		Client proxy = ClientProxy.getClient(serviceV4);
		HTTPConduit conduit = (HTTPConduit) proxy.getConduit();

		HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
		httpClientPolicy.setConnectionTimeout(120000);
		httpClientPolicy.setReceiveTimeout(120000);
		conduit.setClient(httpClientPolicy);

		return serviceV4;
	}

	private static Map<String, Object> getProperties() {
		Map<String,Object> props = new HashMap<String, Object>();
		props.put(PROPERTY_MTOM, Boolean.TRUE);
		return props;
	}
}
