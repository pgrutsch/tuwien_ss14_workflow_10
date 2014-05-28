
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


        while(true) {
            System.out.println("===================");
            System.out.println("Send a SOAP Comment");
            System.out.println("===================");

            System.out.println("Enter email:");
            String email = sc.nextLine();

            System.out.println("Enter comment:");
            String comment = sc.nextLine();

            System.out.println("Enter TVProgram ID:");
            int tvprogramid = Integer.valueOf(sc.nextLine());

            SOAPComment c = new SOAPComment();
            c.setComment(comment);
            c.setEmail(email);
            c.setTvprogram(tvprogramid);

            myService.postComment(c);
        }

    }
}
