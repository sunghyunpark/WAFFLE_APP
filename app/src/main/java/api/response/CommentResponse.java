package api.response;

import java.util.ArrayList;

import model.CommentModel;

/**
 * Created by SungHyun on 2018. 3. 9..
 */

public class CommentResponse {
    private ArrayList<CommentModel> commentList;
    private boolean error;
    private String error_msg;

    public ArrayList<CommentModel> getCommentList() {
        return commentList;
    }

    public void setCommentList(ArrayList<CommentModel> commentList) {
        this.commentList = commentList;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }


}
