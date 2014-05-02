package tvgrabber;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;


/**
 * Created by patrickgrutsch on 30.04.14.
 */
@Component
public class Producer {

    private static final Logger logger = Logger.getLogger(Producer.class);

    @Produce(uri = "seda:ichBinDerStart")
    ProducerTemplate producer;

    public void send(String msg) throws Exception {

        producer.sendBody(msg);

    }

}
