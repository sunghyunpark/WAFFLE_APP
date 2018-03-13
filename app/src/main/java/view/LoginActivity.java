package view;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cafemobile.waffle.MainActivity;
import com.cafemobile.waffle.R;
import com.cafemobile.waffle.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import api.ApiClient;
import api.ApiInterface;
import api.response.LoginResponse;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import database.RealmUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private final static String LOGIN_TYPE_EMAIL = "email";
    private final static String LOGIN_TYPE_FACEBOOK = "facebook";
    private final static String LOGIN_TYPE_KAKAO = "kakao";

    private LoadingDialog loadingDialog;
    private SessionManager sessionManager;
    private FirebaseAuth mAuth;
    private RealmUtil realmUtil;

    @BindView(R.id.email_edit_box) EditText email_et;
    @BindView(R.id.password_edit_box) EditText password_et;
    @BindView(R.id.login_btn) Button login_btn;
    @BindString(R.string.error_not_exist_input_txt) String notExistErrorStr;
    @BindString(R.string.register_error_input_email_txt) String inputEmailErrorStr;
    @BindString(R.string.register_error_input_pw_txt) String inputPwErrorStr;

    @Override
    public void onStop(){
        super.onStop();
        if(loadingDialog != null)
        loadingDialog.dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        init();
    }

    /**
     * init
     */
    private void init(){
        mAuth = FirebaseAuth.getInstance();
        sessionManager = new SessionManager(getApplicationContext());
        realmUtil = new RealmUtil(getApplicationContext());

        loadingDialog = new LoadingDialog(this);
        loadingDialog.getWindow()
                .setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
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
                            Toast.makeText(getApplicationContext(), "Sign In Fail From Firebase",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } } );

    }

    /**
     * Firebase에서 uid 값을 받아온 뒤 Waffle 서버에서 user data 를 받아옴.
     * Realm DB에 User data 저장
     * @param uid
     */
    private void postUserDataForLogin(final String uid){
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<LoginResponse> call = apiService.loginApi("login", uid);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                LoginResponse loginResponse = response.body();
                if(!loginResponse.isError()){
                    realmUtil.InsertUserData(uid, LOGIN_TYPE_EMAIL, mAuth.getCurrentUser().getEmail(), loginResponse.getUser().getName(), loginResponse.getUser().getPhoneNum(), loginResponse.getUser().getCreatedAt());
                    goMainActivity();
                    Toast.makeText(getApplicationContext(), loginResponse.getError_msg(), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), loginResponse.getError_msg(),Toast.LENGTH_SHORT).show();
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

    @OnClick(R.id.login_btn) void loginClicked(){
        String emailStr = email_et.getText().toString().trim();
        String passwordStr = password_et.getText().toString().trim();

        if(emailStr.equals("") || passwordStr.equals("")){
            Toast.makeText(getApplicationContext(), notExistErrorStr, Toast.LENGTH_SHORT).show();
        }else if(!emailStr.contains("@") || !emailStr.contains(".com")){
            Toast.makeText(getApplicationContext(), inputEmailErrorStr,Toast.LENGTH_SHORT).show();
        }else if(passwordStr.length()<6){
            Toast.makeText(getApplicationContext(), inputPwErrorStr, Toast.LENGTH_SHORT).show();
        }else{
            loadingDialog.show();
            signIn(emailStr, passwordStr);
        }
    }
}
