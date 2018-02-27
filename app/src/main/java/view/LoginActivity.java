package view;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cafemobile.waffle.R;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import presenter.LoginPresenter;

public class LoginActivity extends AppCompatActivity {

    private LoadingDialog loadingDialog;

    @BindView(R.id.email_edit_box) EditText email_et;
    @BindView(R.id.password_edit_box) EditText password_et;
    @BindView(R.id.login_btn) Button login_btn;
    @BindString(R.string.error_not_exist_input_txt) String notExistErrorStr;
    @BindString(R.string.register_error_input_email_txt) String inputEmailErrorStr;
    @BindString(R.string.register_error_input_pw_txt) String inputPwErrorStr;

    @Override
    public void onStop(){
        super.onStop();
        loadingDialog.cancel();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        loadingDialog = new LoadingDialog(this);
        loadingDialog .getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

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
            LoginPresenter loginPresenter = new LoginPresenter(getApplicationContext());
            loginPresenter.login(emailStr, passwordStr);
        }
    }
}
