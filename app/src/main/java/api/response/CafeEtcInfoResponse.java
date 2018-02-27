package api.response;

import java.util.ArrayList;

import model.CafeMenuModel;
import model.CommentModel;

/**
 * Created by SungHyun on 2018. 2. 27..
 */

public class CafeEtcInfoResponse {

    private ArrayList<String> cafe_etc_photo_list;
    private ArrayList<CommentModel> comment_text_list;
    private ArrayList<CafeMenuModel> menu_list;
    private boolean like_state;
    private boolean error;
    private String error_msg;


    public ArrayList<String> getCafe_etc_photo_list() {
        return cafe_etc_photo_list;
    }

    public void setCafe_etc_photo_list(ArrayList<String> cafe_etc_photo_list) {
        this.cafe_etc_photo_list = cafe_etc_photo_list;
    }

    public ArrayList<CommentModel> getComment_text_list() {
        return comment_text_list;
    }

    public void setComment_text_list(ArrayList<CommentModel> comment_text_list) {
        this.comment_text_list = comment_text_list;
    }

    public ArrayList<CafeMenuModel> getMenu_list() {
        return menu_list;
    }

    public void setMenu_list(ArrayList<CafeMenuModel> menu_list) {
        this.menu_list = menu_list;
    }

    public boolean isLike_state() {
        return like_state;
    }

    public void setLike_state(boolean like_state) {
        this.like_state = like_state;
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
