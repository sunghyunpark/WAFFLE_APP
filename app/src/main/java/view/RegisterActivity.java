package view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.cafemobile.waffle.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import util.LoginManager;

public class RegisterActivity extends AppCompatActivity {

    private final static String TAG = "RegisterActivity";

    //private LoadingDialog loadingDialog;
    private FirebaseAuth mAuth;

    LoginManager loginManager;

    @BindView(R.id.email_edit_box) EditText email_et;
    @BindView(R.id.email_password_edit_box) EditText password_et;
    @BindView(R.id.name_edit_box) EditText name_et;
    @BindString(R.string.error_not_exist_input_txt) String notExistErrorStr;
    @BindString(R.string.register_error_input_email_txt) String inputEmailErrorStr;
    @BindString(R.string.register_error_input_pw_txt) String inputPwErrorStr;


//    @Override
//    public void onStop(){
//        super.onStop();
//        if(loadingDialog != null)
//        loadingDialog.dismiss();
//    }

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
        loginManager = new LoginManager(getApplicationContext());

        mAuth = FirebaseAuth.getInstance();

        //loadingDialog = new LoadingDialog(this);
        //loadingDialog.getWindow()
                //.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
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
                            loginManager.postUserDataForRegister(LoginManager.LOGIN_TYPE_EMAIL, mAuth.getUid(), name);

                        }else{
                            //if(loadingDialog != null)
                                //loadingDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "이미 동일한 계정이 존재합니다.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }


    @OnClick(R.id.back_btn) void goBack(){
        finish();
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
            //loadingDialog.show();
            createAccount(emailStr, passwordStr, nameStr);
        }
    }

}
