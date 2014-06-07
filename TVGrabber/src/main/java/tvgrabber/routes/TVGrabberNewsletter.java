package tvgrabber.routes;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;

import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tvgrabber.beans.NewsletterBean;
import tvgrabber.beans.NewsletterEnrichAS;
import tvgrabber.beans.NewsletterFullAS;
import tvgrabber.beans.NewsletterTitleAS;
import tvgrabber.entities.Series;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Isabella on 30.04.14.
 */

@Component
public class TVGrabberNewsletter extends RouteBuilder {

    private static final Logger logger = Logger.getLogger(TVGrabberNewsletter.class);

    @Autowired
    private NewsletterBean newsletterbean;

    @Override
    public void configure() throws Exception {
        java.util.Date dateWeek = null;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try{
            dateWeek = dateFormat.parse("2014-04-27 00:00:00");
        }catch(Exception e)
        {
            e.printStackTrace();
        }

        //constant date in long value 27.04 - 2.05
        long week = 1398398400000L+(86400*7*1000);
        Date weekstart = new Date(1398398400000L);
        Date weekend = new Date(week);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String weekstartString = format.format(weekstart);
        String weekendString = format.format(weekend);

        //polls the weekly newsletter from the db
        from("jpa://tvgrabber.entities.Series?consumeDelete=true&consumer.delay=5000&consumer.query=" +
                "select s from tvgrabber.entities.Series s where (s.start >= '" + weekstartString + "' AND s.stop <= '" + weekendString + "')")
                .bean(newsletterbean,"changeHeader")
                .log(LoggingLevel.INFO, "********************** Newsletter INC  **************************")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        logger.info("Newsletter INC: " + exchange.getIn().getBody(Series.class).getTitle() + " " +
                        exchange.getIn().getBody(Series.class).getStart().toString()
                        + " Titel " + exchange.getIn().getHeader("title"));
                    }
                }).to("seda:aggregator");

        //aggregate by title -> for example all columbo to one columbo message
        from("seda:aggregator").aggregate(new NewsletterTitleAS()).header("title")
        .completionInterval(15000)
        .to("seda:resequencer");

         //reverse sort by title
         from("seda:resequencer").resequence(header("title")).batch().timeout(10000).reverse()
                .log(LoggingLevel.INFO, "######################### Resequencer INC  #########################")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        logger.info("Resequencer:" + exchange.getIn().getBody(String.class));

                        exchange.getIn().setHeader("title","fin");
                    }
         }).to("seda:aggregatorAll");

        //aggregate alle message to one
       from("seda:aggregatorAll").aggregate(new NewsletterFullAS()).header("title")
       .completionInterval(15000)
               .log(LoggingLevel.INFO, "********************** Aggregator ALL  **************************")
               .process(new Processor() {
                   @Override
                   public void process(Exchange exchange) throws Exception {
                       logger.info("All INC: " + exchange.getIn().getBody(String.class));
                   }
               })
       .pollEnrich("jpa://tvgrabber.entities.TVGrabberUser?consumeDelete=false&consumer.query=" +
               "Select s from TVGrabberUser s where s.subscribed=True" +
               "", new NewsletterEnrichAS())
       .to("smtps://smtp.gmail.com:465?password=workflow2014&username=workflow2014ss@gmail.com");
    }
}
