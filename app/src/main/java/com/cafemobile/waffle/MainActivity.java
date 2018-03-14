package com.cafemobile.waffle;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import database.RealmConfig;
import database.model.UserVO;
import io.realm.Realm;
import model.UserModel;
import view.CafeListFragment;
import view.HotFragment;
import view.IntroActivity;
import view.LikeCafeFragment;
import view.SettingFragment;

/**
 * 1. 앱 실행 시 MainActivity 가 실행된다
 * 2. mAuth 객체를 통해 Firebase에서 로그인 중인 유저가 있는지 판별
 * 3. 로그인 상태 > Realm에 저장된 user 데이터를 싱글톤 객체에 저장
 * 4. 미로그인 상태 > Intro 화면으로 이동된다.
 */

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private int currentPage = R.id.tab_1;
    @BindView(R.id.tab_1) ViewGroup tabBtn1;
    @BindView(R.id.tab_2) ViewGroup tabBtn2;
    @BindView(R.id.tab_3) ViewGroup tabBtn3;
    @BindView(R.id.tab_4) ViewGroup tabBtn4;
    @BindView(R.id.tab1_img) ImageView tab1_iv;
    @BindView(R.id.tab2_img) ImageView tab2_iv;
    @BindView(R.id.tab3_img) ImageView tab3_iv;
    @BindView(R.id.tab4_img) ImageView tab4_iv;
    @BindView(R.id.tab1_txt) TextView tab1_tv;
    @BindView(R.id.tab2_txt) TextView tab2_tv;
    @BindView(R.id.tab3_txt) TextView tab3_tv;
    @BindView(R.id.tab4_txt) TextView tab4_tv;

    @Override
    public void onStop(){
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();    // Firebase 객체 생성

        final SessionManager sessionManager = new SessionManager(getApplicationContext());

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null || sessionManager.isLoggedIn()) {
                    /*
                    Firebase에서 로그인된 User 가 있는 경우 Realm에 저장된 데이터를 싱글톤에 저장
                     */
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Toast.makeText(getApplicationContext(), "Sign In Success From MainActivity", Toast.LENGTH_SHORT).show();

                    setUserDataFromRealm();

                    InitTabIcon(currentPage);

                    android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
                    android.support.v4.app.FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.main_frame, new CafeListFragment());
                    fragmentTransaction.commit();
                } else {
                    /*
                    Firebase에 로그인된 User가 없는 경우 Intro 화면으로 이동
                     */
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Toast.makeText(getApplicationContext(), "Not exist user data from firebase", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), IntroActivity.class));
                    finish();
                }
            }};
        mAuth.addAuthStateListener(mAuthListener);
    }

    /**
     * Init User Data To UserModel From Realm
     */
    private void setUserDataFromRealm(){
        Realm mRealm;
        RealmConfig realmConfig;
        realmConfig = new RealmConfig();
        mRealm = Realm.getInstance(realmConfig.UserRealmVersion(getApplicationContext()));

        UserVO userVO = mRealm.where(UserVO.class).equalTo("no",1).findFirst();
        UserModel.getInstance().setUid(userVO.getUid());
        UserModel.getInstance().setLoginType(userVO.getLoginType());
        UserModel.getInstance().setEmail(userVO.getEmail());
        UserModel.getInstance().setName(userVO.getName());
        UserModel.getInstance().setPhoneNum(userVO.getPhoneNum());
        UserModel.getInstance().setCreatedAt(userVO.getCreatedAt());

        Log.d("UserData", "UserUid : "+ UserModel.getInstance().getUid());
        Log.d("UserData", "UserLoginType : "+ UserModel.getInstance().getLoginType());
        Log.d("UserData", "UserName : "+ UserModel.getInstance().getName());
        Log.d("UserData", "UserEmail : "+ UserModel.getInstance().getEmail());
        Log.d("UserData", "UserPhone : "+ UserModel.getInstance().getPhoneNum());
        Log.d("UserData", "UserCreated_at : "+ UserModel.getInstance().getCreatedAt());

    }

    /**
     * TabMenu Icon Init Method
     * @param tabId -> Tab Id
     */
    private void InitTabIcon(int tabId){
        tab1_iv.setBackgroundResource(R.mipmap.tab_location_img);
        tab1_tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorMoreGray));
        tab2_iv.setBackgroundResource(R.mipmap.tab_hot_img);
        tab2_tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorMoreGray));
        tab3_iv.setBackgroundResource(R.mipmap.tab_like_img);
        tab3_tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorMoreGray));
        tab4_iv.setBackgroundResource(R.mipmap.tab_setting_img);
        tab4_tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorMoreGray));

        switch (tabId){
            case R.id.tab_1:
                tab1_iv.setBackgroundResource(R.mipmap.tab_selected_location_img);
                tab1_tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                break;

            case R.id.tab_2:
                tab2_iv.setBackgroundResource(R.mipmap.tab_selected_hot_img);
                tab2_tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                break;

            case R.id.tab_3:
                tab3_iv.setBackgroundResource(R.mipmap.tab_selected_like_img);
                tab3_tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                break;

            case R.id.tab_4:
                tab4_iv.setBackgroundResource(R.mipmap.tab_selected_setting_img);
                tab4_tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                break;

        }
    }

    @OnClick({R.id.tab_1, R.id.tab_2, R.id.tab_3, R.id.tab_4}) void click(View v){
        Fragment fragment = null;
        Bundle bundle = new Bundle();
        InitTabIcon(v.getId());

        switch (v.getId()){
            case R.id.tab_1:
                if(currentPage == R.id.tab_1){
                    return;
                }else{
                    currentPage = R.id.tab_1;
                    fragment = new CafeListFragment();
                    bundle.putString("KEY_MSG", "replace");
                    fragment.setArguments(bundle);
                }
                break;
            case R.id.tab_2:
                if(currentPage == R.id.tab_2){
                    return;
                }else{
                    currentPage = R.id.tab_2;
                    fragment = new HotFragment();
                    bundle.putString("KEY_MSG", "replace");
                    fragment.setArguments(bundle);
                }
                break;
            case R.id.tab_3:
                if(currentPage == R.id.tab_3){
                    return;
                }else{
                    currentPage = R.id.tab_3;
                    fragment = new LikeCafeFragment();
                    bundle.putString("KEY_MSG", "replace");
                    fragment.setArguments(bundle);
                }
                break;
            case R.id.tab_4:
                if(currentPage == R.id.tab_4){
                    return;
                }else{
                    currentPage = R.id.tab_4;
                    fragment = new SettingFragment();
                    bundle.putString("KEY_MSG", "replace");
                    fragment.setArguments(bundle);
                }
                break;
        }
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }
}
