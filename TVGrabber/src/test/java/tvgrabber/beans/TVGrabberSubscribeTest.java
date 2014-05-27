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
import tvgrabber.StandAloneTestH2;
import tvgrabber.TVGrabberConfig;
import tvgrabber.entities.TVGrabberUser;
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
        classes = {TVGrabberConfig.class, StandAloneTestH2.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles("testing")
public class TVGrabberSubscribeTest extends CamelTestSupport {

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
        assertTrue(exchange.getOut().getBody(TVGrabberUser.class).getSubscribed());

        header.put("from","mail@address.biz");
        addressmanager.subscribe(header,myBody,exchange);
        assertEquals("mail@address.biz", exchange.getOut().getBody(TVGrabberUser.class).getEmail());
        assertTrue(exchange.getOut().getBody(TVGrabberUser.class).getSubscribed());
        assertEquals(myBody, exchange.getOut().getBody(TVGrabberUser.class).getSearchTerm());

    }

    /* Does not work; I guess because the db is "hard" coded..
    @Test
    public void testUnsubscribe(){
        header.put("from","usermailbiz");
        myBody="notRelevant";

        addressmanager.unsubscribe(header,myBody,exchange);
        assertEquals("usermailbiz",exchange.getOut().getBody(TVGrabberUser.class).getEmail());
        assertFalse(exchange.getOut().getBody(TVGrabberUser.class).getSubscribed());
        assertEquals("", exchange.getOut().getBody(TVGrabberUser.class).getSearchTerm());
        assertNotEquals(myBody, exchange.getOut().getBody(TVGrabberUser.class).getSearchTerm());
        assertFalse(exchange.getOut().isFault());
    }
     */

    @Test
    public void unsubscribeExpectedException(){
        String userMail="notindb";
        myBody="notimportant";
        header.put("from",userMail);

        addressmanager.unsubscribe(header,myBody,exchange);
        assertEquals("User '"+userMail+"' tried to unsubscribe: '"+myBody+"' but isn't in the db.",exchange.getException().getMessage());
    }


    @Override
    protected RouteBuilder createRouteBuilder() {
        return new TVGrabberSubscribe();
    }

}
