package util;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.cafemobile.waffle.MainActivity;
import com.cafemobile.waffle.SessionManager;
import com.google.firebase.auth.FirebaseAuth;

import api.ApiClient;
import api.ApiInterface;
import api.response.LoginResponse;
import database.RealmUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by SungHyun on 2018-03-16.
 */

public class LoginManager {

    public static final String LOGIN_TYPE_EMAIL = "email";
    public static final String LOGIN_TYPE_FACEBOOK = "facebook";

    private Context context;
    private RealmUtil realmUtil;
    private FirebaseAuth mAuth;
    private SessionManager sessionManager;

    public LoginManager(Context context){
        this.context = context;
        this.realmUtil = new RealmUtil(this.context);
        this.mAuth = FirebaseAuth.getInstance();
        this.sessionManager = new SessionManager(this.context);
    }

    /**
     * 서버에서 User 데이터가 존재하는지 확인 후 로그인한다.
     * @param uid
     */
    public void postUserDataForLogin(final String uid){
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<LoginResponse> call = apiService.loginApi("login", uid);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                LoginResponse loginResponse = response.body();
                if(!loginResponse.isError()){
                    realmUtil.InsertUserData(uid, LOGIN_TYPE_FACEBOOK, mAuth.getCurrentUser().getEmail(), loginResponse.getUser().getName(), loginResponse.getUser().getPhoneNum(), loginResponse.getUser().getCreatedAt());
                    goMainActivity();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("tag", t.toString());
                Toast.makeText(context, "네트워크 연결상태를 확인해주세요.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * email 가입인 경우 Firebase 에 먼저 User를 등록한 뒤 얻어온 uid 값을 서버에 보낸다.
     * facebook 가입인 경우 uid를 서버로 등록 시도 > uid가 존재하면 바로 로그인 > uid가 없으면 기존 email과 같이 등록
     * @param loginType LOGIN_TYPE_EMAIL(email) / LOGIN_TYPE_FACEBOOK(facebook)
     * @param uid
     * @param name
     */
    public void postUserDataForRegister(final String loginType, final String uid, final String name){
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<LoginResponse> call = apiService.registerApi("register", uid, name, "N");
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                LoginResponse loginResponse = response.body();
                if(!loginResponse.isError()){
                    realmUtil.InsertUserData(uid, loginType, mAuth.getCurrentUser().getEmail(), name, "", loginResponse.getUser().getCreatedAt());
                    goMainActivity();
                }else{
                    if(loginType.equals(LOGIN_TYPE_FACEBOOK)){
                        postUserDataForLogin(uid);
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("tag", t.toString());
                Toast.makeText(context, "네트워크 연결상태를 확인해주세요.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goMainActivity(){
        sessionManager.setLogin(true);

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
