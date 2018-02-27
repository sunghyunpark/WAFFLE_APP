package api.response;

/**
 * Created by SungHyun on 2018. 2. 27..
 */

public class CommonResponse {
    private boolean error;
    private String error_msg;

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
