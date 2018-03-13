package view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.cafemobile.waffle.MainActivity;
import com.cafemobile.waffle.R;
import com.cafemobile.waffle.SessionManager;
import com.cafemobile.waffle.WaffleApplication;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import api.ApiClient;
import api.ApiInterface;
import api.response.LoginResponse;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import database.RealmUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IntroActivity extends AppCompatActivity {

    private static final String TAG = "IntroActivity";
    private final static String LOGIN_TYPE_FACEBOOK = "facebook";

    WaffleApplication waffleApplication;
    Bitmap resized;
    Bitmap bitmap;
    @BindView(R.id.background_layout) ViewGroup background_layout;
    @BindView(R.id.facebook_btn) LoginButton mSigninFacebookButton;
    @BindView(R.id.register_btn) Button registerBtn;
    @BindView(R.id.login_btn) Button loginBtn;

    CallbackManager mFacebookCallbackManager;
    private RealmUtil realmUtil;
    private SessionManager sessionManager;
    private FirebaseAuth mAuth;
    private LoadingDialog loadingDialog;


    @Override
    public void onStop(){
        super.onStop();
        if(loadingDialog != null)
            loadingDialog.dismiss();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        resized.recycle();
        bitmap.recycle();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        ButterKnife.bind(this);
        init();
        setBackground();
    }

    private void init(){
        loadingDialog = new LoadingDialog(this);
        loadingDialog.getWindow()
                .setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        waffleApplication = (WaffleApplication)getApplicationContext();
        mAuth = FirebaseAuth.getInstance();
        sessionManager = new SessionManager(getApplicationContext());
        realmUtil = new RealmUtil(getApplicationContext());
    }

    private void setBackground(){
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.intro_5_25);
        resized = Bitmap.createScaledBitmap(bitmap, waffleApplication.getDISPLAY_WIDTH(),
                waffleApplication.getDISPLAY_HEIGHT(), true);

        Drawable d = new BitmapDrawable(getResources(), resized);
        background_layout.setBackground(d);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            postUserDataForRegister(user.getUid(), user.getDisplayName());

                            //Toast.makeText(getApplicationContext(), "ok", Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    /**
     * facebook 로그인 시 처음엔 register를 시도 > DB에 존재하는 uid가 있는 경우 로그인으로 시도한다.
     * @param uid
     * @param name
     */
    private void postUserDataForRegister(final String uid, final String name){
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<LoginResponse> call = apiService.registerApi("register", uid, name, "N");
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                LoginResponse loginResponse = response.body();
                if(!loginResponse.isError()){
                    realmUtil.InsertUserData(uid, LOGIN_TYPE_FACEBOOK, mAuth.getCurrentUser().getEmail(), name, "", loginResponse.getUser().getCreatedAt());
                    goMainActivity();
                    //Toast.makeText(getApplicationContext(), loginResponse.getError_msg(), Toast.LENGTH_SHORT).show();
                }else{
                    postUserDataForLogin(uid);
                    //Toast.makeText(getApplicationContext(), loginResponse.getError_msg(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("tag", t.toString());
                Toast.makeText(getApplicationContext(), "네트워크 연결상태를 확인해주세요.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goMainActivity(){
        sessionManager.setLogin(true);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void postUserDataForLogin(final String uid){
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
                    //Toast.makeText(getApplicationContext(), loginResponse.getError_msg(), Toast.LENGTH_SHORT).show();
                }else{
                    //Toast.makeText(getApplicationContext(), loginResponse.getError_msg(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("tag", t.toString());
                Toast.makeText(getApplicationContext(), "네트워크 연결상태를 확인해주세요.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * Email Register Activity 로 이동
     */
    @OnClick(R.id.register_btn) void registerClick(){
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
    }

    /**
     * Email Login Activity 로 이동
     */
    @OnClick(R.id.login_btn) void loginClick(){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.facebook_btn) void facebookClick(){
        loadingDialog.show();
        /*
         * Facebook Login
         */
        mFacebookCallbackManager = CallbackManager.Factory.create();

        mSigninFacebookButton.setReadPermissions("email", "public_profile");
        mSigninFacebookButton.registerCallback(mFacebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AuthCredential credential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
                mAuth.signInWithCredential(credential);
                handleFacebookAccessToken(loginResult.getAccessToken());
                Log.d(TAG, "Facebook login Success.");
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "Facebook login canceled.");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "Facebook Login Error", error);
            }
        });
    }
}
