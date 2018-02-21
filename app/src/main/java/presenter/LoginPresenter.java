package presenter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


/**
 * Created by SungHyun on 2018. 2. 21..
 */

public class LoginPresenter implements Loginable{

    private final static String TAG = "LoginPresenter";
    private FirebaseAuth mAuth;
    Context mContext;

    public LoginPresenter(Context context){
        this.mContext = context;
        this.mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void kakaoLogin(){

    }

    @Override
    public void facebookLogin(){

    }

    @Override
    public void login(String email, String password){

    }

    @Override
    public void register(String email, String password, String name){
        createAccount(email, password);
    }

    /**
     * register to Firebase
     * @param email
     * @param password
     */
    private void createAccount(String email, String password) {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (task.isSuccessful()) {
                            Toast.makeText(mContext, "Sign Up Success",
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(mContext, "Authentication failed",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
}
