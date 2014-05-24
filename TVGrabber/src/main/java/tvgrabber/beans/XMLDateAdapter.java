package tvgrabber.beans;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 24.05.14
 * Time: 12:37
 *
 *  Used to convert guide.xml start- and enddate (String) to java.util.Date
 */
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class XMLDateAdapter extends XmlAdapter<String, Date> {

    // Example input String: "20140428040500 +0200"
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss Z");

    @Override
    public String marshal(Date v) throws Exception {
        return dateFormat.format(v);
    }

    @Override
    public Date unmarshal(String v) throws Exception {
        return dateFormat.parse(v);
    }

}