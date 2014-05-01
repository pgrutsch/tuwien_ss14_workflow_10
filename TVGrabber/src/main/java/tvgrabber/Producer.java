package tvgrabber;

import org.apache.camel.Consume;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import static org.apache.camel.component.dataset.DataSetEndpoint.assertEquals;


/**
 * Created by patrickgrutsch on 30.04.14.
 */
@Component
public class Producer {

    @Produce(uri = "seda:ichBinDerStart")
    ProducerTemplate producer;

    public void send(String msg) throws Exception {

        producer.sendBody(msg);

        //producer.requestBody("hihihihi");

    }

}
