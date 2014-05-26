
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import soap.PostComment;
import soap.SOAPComment;

import java.util.Scanner;

/**
 * Created by patrickgrutsch on 17.05.14.
 */


public class WSClient {



    public static void main(String[] args) {

        WSClient client = new WSClient();
        client.test();

    }

    public void test() {

        String serviceUrl = "http://localhost:8080/spring-soap/PostComment";
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(PostComment.class);
        factory.setAddress(serviceUrl);

        PostComment myService = (PostComment) factory.create();

        Scanner sc = new Scanner(System.in);
        String input;

        System.out.println("=== Enter comment and press enter or enter exit to quit");

        while(sc.hasNext()) {

            input = sc.nextLine();

            if(input.equals("exit")) {
                return;
            } else {
                SOAPComment c = new SOAPComment();
                c.setComment(input);
                c.setEmail("alois@huber.com");
                c.setTvprogram(1);
                myService.postComment(c);
            }


        }

    }
}
