package view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cafemobile.waffle.R;
import com.cafemobile.waffle.SessionManager;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import database.RealmUtil;
import model.UserModel;

public class SettingFragment extends Fragment {

    private SessionManager sessionManager;
    private FirebaseAuth mAuth;
    private RealmUtil realmUtil;

    View v;
    @BindView(R.id.user_name_txt) TextView user_name_tv;
    @BindView(R.id.user_email_txt) TextView user_email_tv;
    @BindView(R.id.user_profile_edit_layout) ViewGroup userProfileEditLayout;
    @BindView(R.id.app_version_txt) TextView app_version_tv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume(){
        super.onResume();

        SetUI();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_setting, container, false);
        ButterKnife.bind(this, v);

        mAuth = FirebaseAuth.getInstance();
        sessionManager = new SessionManager(getContext());
        realmUtil = new RealmUtil(getContext());

        return v;
    }

    private void SetUI(){
        user_name_tv.setText(UserModel.getInstance().getName());
        user_email_tv.setText(UserModel.getInstance().getEmail());

        //앱 버전
        String version;
        try {
            PackageInfo i = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            version = i.versionName;
            app_version_tv.setText("v"+version);
        } catch(PackageManager.NameNotFoundException e) { }

    }

    @OnClick(R.id.logout_btn) void logoutClicked(){
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("로그아웃");
        alert.setMessage("정말 로그아웃 하시겠습니까?");
        alert.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //sessionManager.setLogin(false);
                //realmUtil.DeleteUserData();
                mAuth.signOut();    //Firebase Logout
                sessionManager.setLogin(false);
                realmUtil.DeleteUserData();
                LoginManager.getInstance().logOut();

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

}
