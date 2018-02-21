package presenter;

/**
 * Created by SungHyun on 2018-02-21.
 */

public interface Loginable {
    void kakaoLogin();
    void facebookLogin();
    void register(String email, String password, String name);
    void login(String email, String password);
}
