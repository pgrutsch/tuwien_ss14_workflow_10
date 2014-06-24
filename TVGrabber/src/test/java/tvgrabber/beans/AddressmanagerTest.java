package tvgrabber.beans;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import tvgrabber.TestConfig;
import tvgrabber.entities.TVGrabberUser;
import tvgrabber.exceptions.UnsubscribeException;
import tvgrabber.routes.TVGrabberSubscribe;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Cle
 * Date: 17.05.2014
 */
@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class,
        classes = {TestConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles("testing")
public class AddressmanagerTest extends CamelTestSupport {

    private String myBody;
    private Map<String, Object> header;
    private Exchange exchange;

    @Autowired
    Addressmanager addressmanager;

    @Before
    public void setUp() {
        context = new DefaultCamelContext();
        exchange = new DefaultExchange(context);
        header = new HashMap<String, Object>();

    }

    @Test
    public void testSubscribe(){
        header.put("from","bla");
        myBody="subscribe";
        addressmanager.subscribe(header, myBody, exchange);

        assertEquals("bla", exchange.getOut().getBody(TVGrabberUser.class).getEmail());
        assertTrue(exchange.getOut().getBody(TVGrabberUser.class).isSubscribed());

        header.put("from","mail@address.biz");
        addressmanager.subscribe(header,myBody,exchange);
        assertEquals("mail@address.biz", exchange.getOut().getBody(TVGrabberUser.class).getEmail());
        assertTrue(exchange.getOut().getBody(TVGrabberUser.class).isSubscribed());
        assertEquals(myBody, exchange.getOut().getBody(TVGrabberUser.class).getSearchTerm());

    }

    @Test
    public void testUnsubscribe() throws UnsubscribeException {
        header.put("from","usermailbiz");
        myBody="notRelevant";

        addressmanager.unsubscribe(header,myBody,exchange);
        assertEquals("usermailbiz",exchange.getOut().getBody(TVGrabberUser.class).getEmail());
        assertFalse(exchange.getOut().getBody(TVGrabberUser.class).isSubscribed());
        assertEquals("", exchange.getOut().getBody(TVGrabberUser.class).getSearchTerm());
        assertNotEquals(myBody, exchange.getOut().getBody(TVGrabberUser.class).getSearchTerm());
        assertFalse(exchange.getOut().isFault());
    }

    @Test(expected = UnsubscribeException.class)
    public void unsubscribeExpectedException() throws UnsubscribeException {
        String userMail="notindb";
        myBody="notimportant";
        header.put("from",userMail);

        addressmanager.unsubscribe(header,myBody,exchange);
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new TVGrabberSubscribe();
    }

}
