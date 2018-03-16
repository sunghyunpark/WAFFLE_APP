package view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cafemobile.waffle.R;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import util.LoginManager;

public class IntroActivity extends AppCompatActivity {

    private static final String TAG = "IntroActivity";

    private WaffleApplication waffleApplication;
    private Bitmap resized;
    private Bitmap bitmap;
    @BindView(R.id.background_layout) ViewGroup background_layout;
    @BindView(R.id.facebook_btn) LoginButton mSigninFacebookButton;

    CallbackManager mFacebookCallbackManager;
    private FirebaseAuth mAuth;
    LoginManager loginManager;


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

    /**
     * init
     */
    private void init(){

        loginManager = new LoginManager(getApplicationContext());

        waffleApplication = (WaffleApplication)getApplicationContext();
        mAuth = FirebaseAuth.getInstance();
    }

    private void setBackground(){
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.intro_5_25);
        resized = Bitmap.createScaledBitmap(bitmap, waffleApplication.getDISPLAY_WIDTH(),
                waffleApplication.getDISPLAY_HEIGHT(), true);

        Drawable d = new BitmapDrawable(getResources(), resized);
        background_layout.setBackground(d);
    }

    /**
     * Firebase 에서 로그인 된 User의 데이터를 가져와 postUserDataForRegister를 통해 서버에 저장 시도한다.
     * 서버에 저장 시도 시 이미 저장된 유저이면 로그인 하도록 한다.
     * @param token
     */
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
                            loginManager.postUserDataForRegister(LoginManager.LOGIN_TYPE_FACEBOOK, user.getUid(), user.getDisplayName());
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_SHORT).show();
                        }

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
