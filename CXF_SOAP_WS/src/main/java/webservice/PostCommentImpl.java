package webservice;


import webservice.soap.PostComment;
import webservice.soap.SOAPComment;

public class PostCommentImpl implements PostComment {


    @Override
    public void postComment(SOAPComment parameters) {

        System.out.println("----------- " + parameters.getComment());
    }
}
