package tvgrabber;

import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.Headers;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by patrickgrutsch on 01.05.14.
 */
@Component
public class MyBean {


    public void echo(@Headers Map<String, Object> headers, @Body String myBody, Exchange exchange) {
        System.out.println("mybean echo(); " + myBody);
    }
}
