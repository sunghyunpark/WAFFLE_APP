package api.response;

import model.UserModel;

/**
 * Created by SungHyun on 2018-02-24.
 */

public class LoginResponse {
    private UserModel user;
    private boolean error;
    private String error_msg;

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
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
