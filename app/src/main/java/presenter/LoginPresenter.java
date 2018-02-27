package presenter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.cafemobile.waffle.MainActivity;
import com.cafemobile.waffle.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import api.ApiClient;
import api.ApiInterface;
import api.response.LoginResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by SungHyun on 2018. 2. 21..
 */

public class LoginPresenter implements Loginable{

    private final static String TAG = "LoginPresenter";
    private SessionManager sessionManager;
    private FirebaseAuth mAuth;
    Context mContext;

    public LoginPresenter(Context context){
        this.mContext = context;
        this.mAuth = FirebaseAuth.getInstance();
        this.sessionManager = new SessionManager(context);
    }

    @Override
    public void kakaoLogin(){

    }

    @Override
    public void facebookLogin(){

    }

    @Override
    public void login(String email, String password){
        signIn(email, password);
    }

    @Override
    public void register(String email, String password, String name){
        createAccount(email, password, name);
    }

    /**
     * logout from firebase
     */
    @Override
    public void logout(){
        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
        alert.setTitle("로그아웃");
        alert.setMessage("정말 로그아웃 하시겠습니까?");
        alert.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //sessionManager.setLogin(false);
                //realmUtil.DeleteUserData();
                mAuth.signOut();    //Firebase Logout
                sessionManager.setLogin(false);
            }
        });
        alert.setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.

                    }
                });
        alert.show();
    }

    /**
     * register to Firebase
     * @param email
     * @param password
     */
    private void createAccount(String email, String password, final String name) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (task.isSuccessful()) {
                            Log.d("LoginPresenter", mAuth.getUid());
                            postUserDataForRegister(mAuth.getUid(), name);

                        }else{
                            Toast.makeText(mContext, "Authentication failed",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    /**
     * login to Firebase
     * @param email
     * @param password
     */
    private void signIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            postUserDataForLogin(mAuth.getUid());
                        } else {
                            Toast.makeText(mContext, "Sign In Fail From Firebase",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } } );

    }

    /**
     * Firebase에 회원가입 후 얻은 uid, name 을 Waffle 서버 DB로 전송.
     * Realm DB에 User data 저장
     * @param uid
     * @param name
     */
    private void postUserDataForRegister(String uid, String name){
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<LoginResponse> call = apiService.registerApi("register", uid, name, "N");
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                LoginResponse loginResponse = response.body();
                if(!loginResponse.isError()){
                    goMainActivity();
                    Toast.makeText(mContext, loginResponse.getError_msg(), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(mContext, loginResponse.getError_msg(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("tag", t.toString());
                Toast.makeText(mContext, "네트워크 연결상태를 확인해주세요.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Firebase에서 uid 값을 받아온 뒤 Waffle 서버에서 user data 를 받아옴.
     * Realm DB에 User data 저장
     * @param uid
     */
    private void postUserDataForLogin(String uid){
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<LoginResponse> call = apiService.loginApi("login", uid);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                LoginResponse loginResponse = response.body();
                if(!loginResponse.isError()){
                    goMainActivity();
                    Toast.makeText(mContext, loginResponse.getError_msg(), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(mContext, loginResponse.getError_msg(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("tag", t.toString());
                Toast.makeText(mContext, "네트워크 연결상태를 확인해주세요.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goMainActivity(){
        sessionManager.setLogin(true);

        Intent intent = new Intent(mContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }
}
