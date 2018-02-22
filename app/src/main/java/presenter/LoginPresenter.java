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
        createAccount(email, password);
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
    private void createAccount(String email, String password) {
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
                            Toast.makeText(mContext, "Sign In Success From  LoginPresenter",
                                    Toast.LENGTH_SHORT).show();
                            sessionManager.setLogin(true);

                            Intent intent = new Intent(mContext, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(intent);
                        } else {
                            Toast.makeText(mContext, "Sign In Fail From LoginPresenter",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } } );

    }
}
