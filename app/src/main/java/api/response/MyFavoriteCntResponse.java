package api.response;

/**
 * Created by SungHyun on 2018. 3. 9..
 */

public class MyFavoriteCntResponse {
    private String myFavoriteCafeCnt;
    private String myFavoriteCafeCreated_at;
    private String myFavoriteCafeName;
    private String myCommentCafeCnt;
    private String myCommentCafeCreated_at;
    private String myCommentCafeName;
    private boolean error;
    private String error_msg;

    public String getMyFavoriteCafeCnt() {
        return myFavoriteCafeCnt;
    }

    public void setMyFavoriteCafeCnt(String myFavoriteCafeCnt) {
        this.myFavoriteCafeCnt = myFavoriteCafeCnt;
    }

    public String getMyCommentCafeCnt() {
        return myCommentCafeCnt;
    }

    public void setMyCommentCafeCnt(String myCommentCafeCnt) {
        this.myCommentCafeCnt = myCommentCafeCnt;
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

    public String getMyFavoriteCafeCreated_at() {
        return myFavoriteCafeCreated_at;
    }

    public void setMyFavoriteCafeCreated_at(String myFavoriteCafeCreated_at) {
        this.myFavoriteCafeCreated_at = myFavoriteCafeCreated_at;
    }

    public String getMyCommentCafeCreated_at() {
        return myCommentCafeCreated_at;
    }

    public void setMyCommentCafeCreated_at(String myCommentCafeCreated_at) {
        this.myCommentCafeCreated_at = myCommentCafeCreated_at;
    }

    public String getMyFavoriteCafeName() {
        return myFavoriteCafeName;
    }

    public void setMyFavoriteCafeName(String myFavoriteCafeName) {
        this.myFavoriteCafeName = myFavoriteCafeName;
    }

    public String getMyCommentCafeName() {
        return myCommentCafeName;
    }

    public void setMyCommentCafeName(String myCommentCafeName) {
        this.myCommentCafeName = myCommentCafeName;
    }
}
