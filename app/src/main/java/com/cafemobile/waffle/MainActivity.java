package com.cafemobile.waffle;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import view.CafeListFragment;
import view.HotFragment;
import view.LikeCafeFragment;
import view.SettingFragment;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        InitTabIcon(currentPage);

        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, new CafeListFragment());
        fragmentTransaction.commit();

    }

    /**
     * TabMenu Icon Init Method
     * @param tabId -> Tab Id
     */
    private void InitTabIcon(int tabId){
        tab1_iv.setBackgroundResource(R.mipmap.ic_launcher);
        tab1_tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGray));
        tab2_iv.setBackgroundResource(R.mipmap.ic_launcher);
        tab2_tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGray));
        tab3_iv.setBackgroundResource(R.mipmap.ic_launcher);
        tab3_tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGray));
        tab4_iv.setBackgroundResource(R.mipmap.ic_launcher);
        tab4_tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGray));

        switch (tabId){
            case R.id.tab_1:
                tab1_iv.setBackgroundResource(R.mipmap.ic_launcher);
                tab1_tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                break;

            case R.id.tab_2:
                tab2_iv.setBackgroundResource(R.mipmap.ic_launcher);
                tab2_tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                break;

            case R.id.tab_3:
                tab3_iv.setBackgroundResource(R.mipmap.ic_launcher);
                tab3_tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                break;

            case R.id.tab_4:
                tab4_iv.setBackgroundResource(R.mipmap.ic_launcher);
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
                currentPage = R.id.tab_1;
                fragment = new CafeListFragment();
                bundle.putString("KEY_MSG", "replace");
                fragment.setArguments(bundle);
                break;
            case R.id.tab_2:
                currentPage = R.id.tab_2;
                fragment = new HotFragment();
                bundle.putString("KEY_MSG", "replace");
                fragment.setArguments(bundle);
                break;
            case R.id.tab_3:
                currentPage = R.id.tab_3;
                fragment = new LikeCafeFragment();
                bundle.putString("KEY_MSG", "replace");
                fragment.setArguments(bundle);
                break;
            case R.id.tab_4:
                currentPage = R.id.tab_4;
                fragment = new SettingFragment();
                bundle.putString("KEY_MSG", "replace");
                fragment.setArguments(bundle);
                break;
        }
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }
}
