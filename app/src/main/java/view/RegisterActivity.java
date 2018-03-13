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

public class RegisterActivity extends AppCompatActivity {

    private final static String TAG = "RegisterActivity";
    private final static String LOGIN_TYPE_EMAIL = "email";
    private final static String LOGIN_TYPE_FACEBOOK = "facebook";
    private final static String LOGIN_TYPE_KAKAO = "kakao";

    private LoadingDialog loadingDialog;
    private SessionManager sessionManager;
    private FirebaseAuth mAuth;
    private RealmUtil realmUtil;

    @BindView(R.id.register_btn) Button register_btn;
    @BindView(R.id.email_edit_box) EditText email_et;
    @BindView(R.id.email_password_edit_box) EditText password_et;
    @BindView(R.id.name_edit_box) EditText name_et;
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
        setContentView(R.layout.activity_register);

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
                            if(loadingDialog != null)
                                loadingDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Authentication failed",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    /**
     * Firebase에 회원가입 후 얻은 uid, name 을 Waffle 서버 DB로 전송.
     * Realm DB에 User data 저장
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
                    realmUtil.InsertUserData(uid, LOGIN_TYPE_EMAIL, mAuth.getCurrentUser().getEmail(), name, "", loginResponse.getUser().getCreatedAt());
                    goMainActivity();
                    Toast.makeText(getApplicationContext(), loginResponse.getError_msg(), Toast.LENGTH_SHORT).show();
                }else{
                    if(loadingDialog != null)
                        loadingDialog.dismiss();
                    Toast.makeText(getApplicationContext(), loginResponse.getError_msg(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("tag", t.toString());
                if(loadingDialog != null)
                    loadingDialog.dismiss();
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

    @OnClick(R.id.register_btn) void registerClicked(){
        String emailStr = email_et.getText().toString().trim();
        String passwordStr = password_et.getText().toString().trim();
        String nameStr = name_et.getText().toString().trim();

        if(emailStr.equals("") || passwordStr.equals("")){
            Toast.makeText(getApplicationContext(), notExistErrorStr, Toast.LENGTH_SHORT).show();
        }else if(!emailStr.contains("@") || !emailStr.contains(".com")){
            Toast.makeText(getApplicationContext(), inputEmailErrorStr,Toast.LENGTH_SHORT).show();
        }else if(passwordStr.length()<6){
            Toast.makeText(getApplicationContext(), inputPwErrorStr, Toast.LENGTH_SHORT).show();
        }else{
            loadingDialog.show();
            createAccount(emailStr, passwordStr, nameStr);
        }

    }


}
