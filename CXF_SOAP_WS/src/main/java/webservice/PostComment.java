package webservice;

import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * This class was generated by Apache CXF 2.7.11
 * 2014-05-18T23:23:25.602+02:00
 * Generated source version: 2.7.11
 * 
 */
@WebService(targetNamespace = "http://www.tvgrabber.com/soap", name = "PostComment")
@XmlSeeAlso({ObjectFactory.class})
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface PostComment {

    @Oneway
    @WebMethod(operationName = "PostComment", action = "http://www.tvgrabber.com/soap/PostComment")
    public void postComment(
            @WebParam(partName = "parameters", name = "Comment", targetNamespace = "http://www.tvgrabber.com/soap")
            Comment parameters
    );
}
