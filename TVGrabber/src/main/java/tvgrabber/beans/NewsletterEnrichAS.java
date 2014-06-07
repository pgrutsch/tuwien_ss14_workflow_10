package tvgrabber.beans;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.log4j.Logger;
import tvgrabber.entities.TVGrabberUser;

/**
 * Created with IntelliJ IDEA.
 * User: Isabella
 * Date: 05.06.14
 * Time: 15:04
 * To change this template use File | Settings | File Templates.
 */
public class NewsletterEnrichAS implements AggregationStrategy
{
    private static final Logger logger = Logger.getLogger(NewsletterEnrichAS.class);

        public Exchange aggregate(Exchange original, Exchange resource) {
            //searchTerm
            String[] userSearchtearm = resource.getIn().getBody(TVGrabberUser.class).getSearchTerm().split(";");
            Boolean bgef=false;
            String sMailadressen="";
            String sBody = original.getIn().getBody(String.class).replaceAll("\n", "");

            String[] splitarray = sBody.split("\\$");

            String[] serien= new String[(splitarray.length/2)];

            for(int i=1,k=0;i<splitarray.length-1;i=i+2,k++)
            {
                serien[k]=splitarray[i];
            }

            for(int i=0;i<userSearchtearm.length && bgef==false;i++)
            {
                for(int j=0;j<serien.length && bgef==false;j++)
                {
                    if(userSearchtearm[i].toLowerCase().trim().equals(serien[j].toLowerCase().trim()))
                    {
                        bgef=true;
                    }
                }
            }

            if(bgef==true)
            {
                if(original.getIn().getHeader("To")!=null)
                {
                    sMailadressen=original.getIn().getHeader("To").toString();
                }
                sMailadressen=sMailadressen+resource.getIn().getBody(TVGrabberUser.class).getEmail();
                original.getIn().setHeader("To",sMailadressen);
                original.getIn().setHeader("Subject","Newsletter TVGrabber");
            }

            original.getIn().setBody(original.getIn().getBody(String.class));

            return original;
        }
}
