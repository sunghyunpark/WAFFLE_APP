package api.response;

import java.util.ArrayList;

import model.CafeModel;

/**
 * Created by SungHyun on 2018-02-26.
 */

public class CafeResponse {
    private ArrayList<CafeModel> cafeList;
    //private ArrayList<RecentCommentModel> recentCommentList;
    private String last_cafe_id;
    private boolean error;
    private String error_msg;

    public ArrayList<CafeModel> getCafeList() {
        return cafeList;
    }

    public void setCafeList(ArrayList<CafeModel> cafeList) {
        this.cafeList = cafeList;
    }

    public String getLast_cafe_id() {
        return last_cafe_id;
    }

    public void setLast_cafe_id(String last_cafe_id) {
        this.last_cafe_id = last_cafe_id;
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
