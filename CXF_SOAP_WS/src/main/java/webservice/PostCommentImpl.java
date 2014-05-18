package webservice;

import at.tvgrabber.tns.Comment;
import at.tvgrabber.tns.PostComment;


public class PostCommentImpl implements PostComment {


    @Override
    public void postComment(Comment parameters) {
        System.out.println("============== POST COMMENT: " + parameters.getContent());
    }
}
