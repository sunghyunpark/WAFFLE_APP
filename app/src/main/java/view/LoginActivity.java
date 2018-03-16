package view;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

import com.cafemobile.waffle.MainActivity;
import com.cafemobile.waffle.R;
import com.cafemobile.waffle.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import util.LoginManager;

public class LoginActivity extends AppCompatActivity {

    private LoadingDialog loadingDialog;
    private SessionManager sessionManager;
    private FirebaseAuth mAuth;

    @BindView(R.id.email_edit_box) EditText email_et;
    @BindView(R.id.password_edit_box) EditText password_et;
    @BindString(R.string.error_not_exist_input_txt) String notExistErrorStr;
    @BindString(R.string.register_error_input_email_txt) String inputEmailErrorStr;
    @BindString(R.string.register_error_input_pw_txt) String inputPwErrorStr;

    LoginManager loginManager;

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
        loginManager = new LoginManager(getApplicationContext());

        mAuth = FirebaseAuth.getInstance();
        sessionManager = new SessionManager(getApplicationContext());

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
                            loginManager.postUserDataForLogin(mAuth.getUid());
                        } else {
                            Toast.makeText(getApplicationContext(), "Sign In Fail From Firebase",
                                    Toast.LENGTH_SHORT).show();
                            if(loadingDialog != null)
                                loadingDialog.dismiss();
                        }
                    } } );

    }

    private void goMainActivity(){
        sessionManager.setLogin(true);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @OnClick(R.id.back_btn) void goBack(){
        finish();
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
