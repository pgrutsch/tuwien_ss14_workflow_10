package tvgrabber.beans;

import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

/**
 * Created by LeBon on 25.05.14.
 */
@Component
public class NewSeries {


    private int count = 1;

    /**
     * checks if serie is new
     * @param exchange
     * @return
     */
    public boolean filterExistingSeries(Exchange exchange) {
        //TODO implement
        if (count == 1) {
            count++;
            return true;
        }           else{
            return false;
        }
    }

}
